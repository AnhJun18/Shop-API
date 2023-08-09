package com.myshop.api.service.order;

import com.myshop.api.base.CRUDBaseServiceImpl;
import com.myshop.api.base.ProcedureNames;
import com.myshop.api.payload.request.order.OrderDetailRequest;
import com.myshop.api.payload.request.order.OrderRequest;
import com.myshop.api.payload.response.order.OrderResponse;
import com.myshop.common.EnumCommon;
import com.myshop.common.http.ApiResponse;
import com.myshop.common.http.CodeStatus;
import com.myshop.common.http.ListResponse;
import com.myshop.common.utils.JsonUtils;
import com.myshop.repositories.common.GlobalOption;
import com.myshop.repositories.order.entities.Order;
import com.myshop.repositories.order.entities.OrderDetail;
import com.myshop.repositories.order.entities.Status;
import com.myshop.repositories.order.repos.OrderDetailRepository;
import com.myshop.repositories.order.repos.OrderRepository;
import com.myshop.repositories.order.repos.StatusRepository;
import com.myshop.repositories.product.entities.ProductDetail;
import com.myshop.repositories.product.repos.ProductDetailRepository;
import com.myshop.repositories.user.entities.Customer;
import com.myshop.repositories.user.entities.Employee;
import com.myshop.repositories.user.repos.CustomerRepository;
import com.myshop.repositories.user.repos.EmployeeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.webjars.NotFoundException;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureQuery;
import java.util.*;

@Slf4j
@Transactional
@Service
public class OrderServiceImpl extends CRUDBaseServiceImpl<Order, OrderRequest, OrderResponse, Long> implements OrderService {

    private final OrderRepository orderRepository;
    private final EntityManager entityManager;
    @Autowired
    private ProductDetailRepository productDetailRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private StatusRepository statusRepository;


    public OrderServiceImpl(OrderRepository orderRepository, EntityManager entityManager) {
        super(Order.class, OrderRequest.class, OrderResponse.class, orderRepository);
        this.orderRepository = orderRepository;
        this.entityManager = entityManager;
    }

    @Transactional
    @Override
    public OrderResponse order(String orderUser, OrderRequest orderRequest) {
        Integer result = 500;
        String message = "";
        Order newOrder = null;
        Optional<Customer> userInfo = customerRepository.findByEmail(orderUser);
        if (!userInfo.isPresent()) {
            result = 504;
            message = "Tài khoản không hợp lệ";
        } else {
            try {
                newOrder = Order.builder()
                        .note(orderRequest.getNote())
                        .nameReceiver(orderRequest.getNameReceiver())
                        .phoneReceiver(orderRequest.getPhoneReceiver())
                        .address(orderRequest.getAddressReceiver())
                        .status(statusRepository.findByName(EnumCommon.EStatus.WAITPAYMENT.getName()))
                        .customerId(userInfo.get().getId())
                        .build();
                orderRepository.save(newOrder);
                for (OrderDetailRequest item : orderRequest.getOrderItems()) {
                    Optional<ProductDetail> productDetail = productDetailRepository.findById(item.getProductDetailId());
                    if (item.getQuantity() <= 0)
                        throw new Exception("Số lượng mua không hợp lệ");
                    if (productDetail.get().getQuantity() < item.getQuantity())
                        throw new Exception("Sản phẩm " + productDetail.get().getProduct() + " không đủ số lượng tồn!");
                    orderDetailRepository
                            .save(
                                    OrderDetail.builder()
                                            .productDetail(productDetail.get())
                                            .order(newOrder)
                                            .quantityOrder(item.getQuantity())
                                            .priceSale(item.getPrice())
                                            .build());
                    result = 200;
                    message = "Đơn hàng đã được tạo thành công";
                }
            } catch (Exception e) {
                result = 504;
                message = "Lỗi tạo đợn hàng " + e.getMessage();
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return OrderResponse.builder().message(message).data(null).status(result).build();
            }
        }
        return OrderResponse.builder().message(message).data(newOrder).status(result).build();
    }

    @Override
    public ApiResponse<?> getStateOrder(Long orderId) {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("SP_ORDER_GetState");
        query.registerStoredProcedureParameter("ORDERID", Long.class, ParameterMode.IN);
        query.setParameter("ORDERID",orderId);
        query.execute();
        List<Object[]> data = query.getResultList();
        if(data.size()==0){
           return ApiResponse.fromErrorCode(CodeStatus.CODE_NOT_EXIST);
        }
        Object[] orderRes = data.get(0);

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("id", orderRes[0]);
        resultMap.put("nameReceiver", orderRes[1]);
        resultMap.put("phoneReceiver", orderRes[2]);
        resultMap.put("address", orderRes[3]);
        resultMap.put("billId", orderRes[4]);
        resultMap.put("totalMoney", orderRes[5]);
        return ApiResponse.of(resultMap);
    }

    @Override
    public ApiResponse<?> getHistoryOrderByStatus(String user, String statusName) {
        try{
            Customer customer = customerRepository.findByEmail(user).get();
            Status status =statusRepository.findByName(statusName);
            List<Order> orders=orderRepository.findAllByCustomerIdAndStatus_Id(customer.getId(),status.getId());
            List<Map<String,Object>> resMap= new ArrayList<>();
            for (Order od:orders) {
                Map<String,Object> map=new HashMap<>();
                map.put("orderId",od.getOrderId());
                map.put("billCode",od.getBill());
                StoredProcedureQuery query = entityManager.createStoredProcedureQuery("SP_ORDER_GetDetailOrder")
                        .registerStoredProcedureParameter("ORDERID", Long.class, ParameterMode.IN);
                query.setParameter("ORDERID", od.getOrderId());
                query.execute();
                List<Object[]> orderDetailList = query.getResultList();
                List<Map<String,Object>> details = new ArrayList<>();
                for (Object[] ob:orderDetailList) {
                    Map<String,Object> newDetail = new HashMap<>();
                    newDetail.put("productId",ob[0]);
                    newDetail.put("productName",ob[1]);
                    newDetail.put("defaultImage",ob[2]);
                    newDetail.put("size",ob[3]);
                    newDetail.put("color",ob[4]);
                    newDetail.put("productDetailId",ob[6]);
                    newDetail.put("salePrice",ob[7]);
                    newDetail.put("quantityOrder",ob[8]);
                    newDetail.put("returnFormId",ob[9]);
                    newDetail.put("quantityReturn",ob[10]);
                    newDetail.put("quantityInventory",ob[11]);
                    newDetail.put("amountOrder",ob[12]);
                    details.add(newDetail);
                }
                map.put("details",details);
                resMap.add(map);
            }
            return ApiResponse.builder().status(200).data(resMap).build();
        }catch (Exception e){
            return ApiResponse.builder().status(505).data(null).build();
        }
    }

    @Override
    public ApiResponse<?> getListOrder(String search,Integer statusId,String fromDate, String toDate, Integer page, Integer itemsPerPage) {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery(ProcedureNames.SP_ORDER_GetList)
                .registerStoredProcedureParameter("PAGE", Integer.class, ParameterMode.IN)
                .registerStoredProcedureParameter("PAGESIZE", Integer.class, ParameterMode.IN)
                .registerStoredProcedureParameter("SEARCH", String.class, ParameterMode.IN)
                .registerStoredProcedureParameter("STATUSID", Integer.class, ParameterMode.IN)
                .registerStoredProcedureParameter("CREATEDFROM", String.class, ParameterMode.IN)
                .registerStoredProcedureParameter("CREATEDTO", String.class, ParameterMode.IN)
                .registerStoredProcedureParameter("TOTALITEM", Integer.class, ParameterMode.OUT);
        query.setParameter("PAGE", page);
        query.setParameter("PAGESIZE", itemsPerPage);
        query.setParameter("SEARCH", search);
        query.setParameter("STATUSID", statusId);
        query.setParameter("CREATEDFROM", fromDate);
        query.setParameter("CREATEDTO", toDate);

        query.execute();
        int totalItem = (int) query.getOutputParameterValue("TOTALITEM");
        List<Object[]> orderList = query.getResultList();
        List<Map<String,Object>> resMap= new ArrayList<>();
        for (Object[] ob:orderList ) {
            Map<String,Object> item= new HashMap<>();
            item.put("orderId",ob[0]);
            item.put("note",ob[1]);
            item.put("address",ob[2]);
            item.put("nameReceiver",ob[3]);
            item.put("phoneReceiver",ob[4]);
            item.put("statusId",ob[5]);
            item.put("createdDate",ob[10]);
            item.put("statusName",ob[11]);
            item.put("pickerName",ob[12]);
            item.put("totalMoney",ob[13]);
            resMap.add(item);
        }
        return ApiResponse.of(ListResponse.builder()
                .page(page)
                .totalItems(totalItem)
                .totalPages((int) Math.ceil(totalItem * 1.0 / itemsPerPage))
                .itemsPerPage(itemsPerPage)
                .items(resMap)
                .build());
    }

    @Override
    public ApiResponse<?> getOptionStatus() {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("CBO_GetOption", GlobalOption.class);
        query.registerStoredProcedureParameter("TABLENAME", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("COLUMNID", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("COLUMNNAME", String.class, ParameterMode.IN);
        query.setParameter("TABLENAME", "STATUS");
        query.setParameter("COLUMNID", "STATUSID");
        query.setParameter("COLUMNNAME", "STATUSNAME");

        query.execute();
        List<GlobalOption> options = query.getResultList();

        return ApiResponse.of(options);
    }

    @Override
    public ApiResponse<?> getDetailById(Long orderId) {

        StoredProcedureQuery queryOrder = entityManager.createStoredProcedureQuery("SP_ORDER_GetOrderById")
                .registerStoredProcedureParameter("ORDERID", Long.class, ParameterMode.IN);
        queryOrder.setParameter("ORDERID", orderId);
        queryOrder.execute();
        Object[] order = (Object[]) queryOrder.getSingleResult();
        Map<String, Object> resMap= new HashMap<>();
        resMap.put("orderId",order[0]);
        resMap.put("nameReceiver",order[1]);
        resMap.put("phoneReceiver",order[2]);
        resMap.put("address",order[3]);
        resMap.put("note",order[4]);
        resMap.put("statusId",order[5]);
        resMap.put("statusName",order[6]);
        resMap.put("totalMoney",order[7]);
        resMap.put("customerPicker",order[8]);
        resMap.put("employeeDeliver",order[9]);
        resMap.put("billCode",order[10]);
        resMap.put("orderDate",order[11]);
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("SP_ORDER_GetDetailOrder")
                .registerStoredProcedureParameter("ORDERID", Long.class, ParameterMode.IN);
        query.setParameter("ORDERID", orderId);
        query.execute();
        List<Object[]> orderDetailList = query.getResultList();
        List<Map<String,Object>> details = new ArrayList<>();
        for (Object[] ob:orderDetailList) {
            Map<String,Object> newDetail = new HashMap<>();
            newDetail.put("productId",ob[0]);
            newDetail.put("productName",ob[1]);
            newDetail.put("defaultImage",ob[2]);
            newDetail.put("size",ob[3]);
            newDetail.put("color",ob[4]);
            newDetail.put("productDetailId",ob[6]);
            newDetail.put("salePrice",ob[7]);
            newDetail.put("quantityOrder",ob[8]);
            newDetail.put("returnFormId",ob[9]);
            newDetail.put("quantityReturn",ob[10]);
            newDetail.put("quantityInventory",ob[11]);
            newDetail.put("amountOrder",ob[12]);
            details.add(newDetail);
        }
        resMap.put("details",details);

        return ApiResponse.of(resMap);
    }

    @Transactional
    @Override
    public ApiResponse<?> confirmOrder(Long orderId, String employeeId) {
        Optional<Order> order = orderRepository.findById(orderId);
        Optional<Employee> employee= employeeRepository.findByEmail(employeeId);
        if(!order.isPresent())
            return ApiResponse.fromErrorCode(CodeStatus.CODE_NOT_EXIST);
        order.get().setEmployeeReview(employee.get().getId());
        order.get().setStatus(statusRepository.findByName(EnumCommon.EStatus.PAPERING.getName()));
        orderRepository.save(order.get());
        return ApiResponse.builder().status(200).message("Đơn hàng đã được duyệt").build();
    }

    @Override
    public ApiResponse<?> addDeliveryOrder(Long orderId, String employeeDelivery) {
        Map<String,Object> data=JsonUtils.readToMap(employeeDelivery);
        Long employeeId= Long.parseLong(data.get("employeeDelivery").toString()) ;
        employeeRepository.findById(employeeId).get();
        Order order = orderRepository.findById(orderId).get();
        order.setEmployeeDelivery(employeeId);
        order.setStatus(statusRepository.findByName(EnumCommon.EStatus.DELIVERING.getName()));
        orderRepository.save(order);
        return ApiResponse.builder().status(200).message("Đơn hàng đã được phân công").build();
    }

    @Override
    public ApiResponse<?> confirmDeliveryOrder(Long orderId) {
        Order order = orderRepository.findById(orderId).get();
        order.setStatus(statusRepository.findByName(EnumCommon.EStatus.DONE.getName()));
        orderRepository.save(order);
        return ApiResponse.builder().status(200).message("Đơn hàng đã hoàn thành").build();
    }

    @Override
    public ApiResponse<?> cancelOrderById(String user, Long orderId) {
        try {
            Customer customer = customerRepository.findByEmail(user).get();
            Optional<Order> orderOptional = orderRepository.findOrderByCustomerIdAndOrderId(customer.getId(),orderId);
            if(!orderOptional.isPresent())
                throw new NotFoundException("Không tìm thấy đơn hàng");
            orderOptional.get().setStatus(statusRepository.findByName(EnumCommon.EStatus.CANCEL.getName()));
            orderRepository.save(orderOptional.get());
            return  ApiResponse.builder().status(200).data(orderOptional.get()).build();

        }catch (Exception e){
            return  ApiResponse.builder().status(505).data(e.getMessage()).build();
        }

    }

}