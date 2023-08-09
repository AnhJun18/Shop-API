package com.myshop.api.service.payment;

import com.myshop.api.config.VnPayConfig;
import com.myshop.common.EnumCommon;
import com.myshop.repositories.order.entities.Bill;
import com.myshop.repositories.order.entities.Order;
import com.myshop.repositories.order.entities.OrderDetail;
import com.myshop.repositories.order.repos.BillRepository;
import com.myshop.repositories.order.repos.OrderDetailRepository;
import com.myshop.repositories.order.repos.OrderRepository;
import com.myshop.repositories.order.repos.StatusRepository;
import com.myshop.repositories.product.entities.ProductDetail;
import com.myshop.repositories.product.repos.ProductDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureQuery;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class VnPayServiceImpl implements VnPayService {
    @Autowired
    OrderRepository orderRepository;

    @Autowired
    OrderDetailRepository orderDetailRepository;

    @Autowired
    ProductDetailRepository productDetailRepository;

    @Autowired
    StatusRepository statusRepository;
    @Autowired
    BillRepository billRepository;
    @Autowired
    VnPayConfig vnPayConfig;
    private final EntityManager entityManager;

    VnPayServiceImpl(EntityManager entityManager){
        this.entityManager=entityManager;
    }

    @Transactional
    public String createPayment(ServerHttpRequest request, Long order_id) throws UnsupportedEncodingException {
        String vnp_Version = vnPayConfig.vnp_Version;
        String vnp_Command = vnPayConfig.vnp_Command;
        StoredProcedureQuery queryOrder = entityManager.createStoredProcedureQuery("SP_ORDER_GetOrderById")
                .registerStoredProcedureParameter("ORDERID", Long.class, ParameterMode.IN);
        queryOrder.setParameter("ORDERID", order_id);
        queryOrder.execute();
        Object[] client_order = (Object[]) queryOrder.getSingleResult();
//        if (!client_order.isPresent()) {
//            return vnPayConfig.url_response_ui + "status=false" + "&message= Đơn hàng đã được thanh toán" + "&order_id=";
//        }
        String vnp_TxnRef = order_id + vnPayConfig.vnp_Salt + vnPayConfig.getRandomNumber(8);
//        System.out.println(vnp_TxnRef);
        String vnp_IpAddr = vnPayConfig.getIpAddress(request);
        String vnp_TmnCode = vnPayConfig.vnp_TmnCode;

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        double amount = Double.parseDouble(client_order[7].toString());
        vnp_Params.put("vnp_Amount", String.valueOf((int) (amount * 100)));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_BankCode", vnPayConfig.vnp_BankCode);
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang " + order_id);
        vnp_Params.put("vnp_OrderType", vnPayConfig.vnp_OrderType);
        vnp_Params.put("vnp_Locale", vnPayConfig.vnp_Locale);
        vnp_Params.put("vnp_ReturnUrl", vnPayConfig.vnp_Returnurl);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        // Build data to hash and querystring
        String vnp_SecureHash = null;
        try {
            vnp_SecureHash = vnPayConfig.hashAllFields(vnp_Params);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder query = new StringBuilder();
        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                // Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                if (itr.hasNext()) {
                    query.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = vnPayConfig.vnp_PayUrl + "?" + queryUrl;
        return paymentUrl;
    }


    @Transactional
    @GetMapping(value = "/result/VnPayIPN")
    public String resultPayment(ServerHttpRequest request) throws UnsupportedEncodingException {
        Map<String, String> vnp_param = request.getQueryParams().toSingleValueMap();
        String vnp_SecureHash = vnp_param.get("vnp_SecureHash");
        Long order_id = Long.valueOf(vnp_param.get("vnp_TxnRef").toString().split(vnPayConfig.vnp_Salt)[0]);
        Order client_order = orderRepository.findById(order_id).get();

        // Check checksum
        vnp_param.remove("vnp_SecureHash");
        String signValue = vnPayConfig.hashAllFields(vnp_param);
        if (signValue.equals(vnp_SecureHash)) {
            String vnp_TransactionNo = vnp_param.get("vnp_TransactionNo");
            String vnp_ResponseCode = vnp_param.get("vnp_ResponseCode");
            if (!vnp_ResponseCode.equals("00")) {
                return vnPayConfig.url_response_ui + client_order.getOrderId() + "?status=false" + "&message=Giao dịch không thành công";
            } else {
                client_order.setStatus(statusRepository.findByName(EnumCommon.EStatus.WAITCONFIRM.getName()));
                Bill createBill = Bill.builder()
                        .orderId(orderRepository.findById(client_order.getOrderId()).get())
                        .billId(vnp_param.get("vnp_BankTranNo"))
                        .build();
                billRepository.save(createBill);
                List<OrderDetail> orderDetails=orderDetailRepository.findAllByOrder_OrderId(order_id);
                System.out.println(orderDetails.size());
                for (OrderDetail odt:orderDetails ) {
                    Long quantityBefore=odt.getProductDetail().getQuantity();
                    ProductDetail proDetail=odt.getProductDetail();
                    proDetail.setQuantity(quantityBefore-odt.getQuantityOrder());
                    productDetailRepository.save(proDetail);
                }
                orderRepository.save(client_order);
            }
            return vnPayConfig.url_response_ui + client_order.getOrderId() + "?status=true" + "&message=Giao dịch thành công" + "&vnp_TransactionNo=" + vnp_TransactionNo;

        } else
            return vnPayConfig.url_response_ui + client_order.getOrderId() + "?status=false" + "&message=Sai chữ ký";
    }
}