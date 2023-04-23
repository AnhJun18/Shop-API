package com.myshop.api.service.shipment;

import com.myshop.api.config.GHTKConfig;
import com.myshop.api.payload.response.ship.ItemProduct;
import com.myshop.api.payload.response.ship.ShipFeeResponse;
import com.myshop.repositories.order.entities.Order;
import com.myshop.repositories.order.entities.OrderDetail;
import com.myshop.repositories.order.repos.OrderRepository;
import com.myshop.repositories.payment.entities.Epay;
import com.myshop.repositories.payment.entities.Payment;
import com.myshop.repositories.payment.repos.PaymentRepository;
import com.myshop.repositories.shipment.repos.ShipmentRepository;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service

public class GHTKServiceImpl implements  GHTKService{

    @Autowired
    OrderRepository orderRepository;
    @Autowired
    ShipmentRepository shipmentRepository;
    @Autowired
    PaymentRepository paymentRepository;


    public ShipFeeResponse calculateShipFee(String province,String district,
                                   Double weight,String deliver_option, Long value) throws URISyntaxException, IOException {
        JSONObject json = new JSONObject();
        json.put("pick_province", GHTKConfig.PICK_PROVINCE);
        json.put("pick_district", GHTKConfig.PICK_DISTRICT);
        json.put("province", province);
        json.put("district", district);
        json.put("weight", weight);
        json.put("deliver_option", deliver_option);
        json.put("value", value);
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet get = new HttpGet(GHTKConfig.CALCUATE_SHIP_FEE_URL);
        List<NameValuePair> params = new ArrayList<>();
        for (Map.Entry<String, Object> e : json.toMap().entrySet()) {
            params.add(new BasicNameValuePair(e.getKey(), e.getValue().toString()));
        }

        URI uri = new URIBuilder(get.getURI()).setParameters(params).build();
        get.setURI(uri);
        get.setHeader("Token", GHTKConfig.TOKEN);
        CloseableHttpResponse res = client.execute(get);
        BufferedReader rd = new BufferedReader(new InputStreamReader(res.getEntity().getContent()));
        StringBuilder resultJsonStr = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            resultJsonStr.append(line);
        }
        JSONObject result = new JSONObject(resultJsonStr.toString());
        JSONObject fee = (JSONObject) result.get("fee");
        Long only_ship_fee = Long.parseLong(fee.get("ship_fee_only").toString());
        Long total_ship_fee = Long.parseLong(fee.get("fee").toString());
        Long insurance_fee = Long.parseLong(fee.get("insurance_fee").toString());
        String message = result.get("message").toString();
        Boolean isSuccess = (Boolean) result.get("success");
        ShipFeeResponse ship = ShipFeeResponse.builder()
                .only_ship_fee(only_ship_fee)
                .total_ship_fee(total_ship_fee)
                .insurance_fee(insurance_fee)
                .message(message).success(isSuccess).build();
        return ship;
    }

    @Transactional
    public Map<String, Object> createOrder(Long order_id)  throws  IOException {
        JSONObject dataBody = new JSONObject();
        Order od= orderRepository.findOrderById(order_id);
        JSONObject order = new JSONObject();
        order.put("id",od.getId().toString());
        order.put("pick_name", "Shop Ptit");
        order.put("pick_money", od.getTotalPrices());
        order.put("pick_address", "97 đường Man Thiện");
        order.put("pick_province", "Thành Phố Hồ Chí Minh");
        order.put("pick_district", "Thành Phố Thủ Đức");
        order.put("pick_ward", "Phường Tăng Nhơn Phú a");
        order.put("pick_tel", "0988888888");
        order.put("name",  od.getShipment().getNameReceiver());
        order.put("address", od.getShipment().getAddress());
        order.put("province", od.getShipment().getProvince());
        order.put("district", od.getShipment().getDistrict());
        order.put("ward", od.getShipment().getWard());
        order.put("hamlet", "Khác");
        order.put("tel", od.getShipment().getPhoneReceiver());
        if(od.getPayment()==null){
            order.put("is_freeship","0");
        }else {
            order.put("is_freeship","1");
        }
        order.put("email",  od.getUserInfo().getAccount().getEmail());
        order.put("value", od.getTotalPrices());

        List<ItemProduct> list = new ArrayList<ItemProduct>();
        for (OrderDetail detail : od.getOrderDetails()) {
            ItemProduct item = ItemProduct.builder()
                    .name(detail.getProductDetail().getInfoProduct().getName())
                    .weight(0.2)
                    .quantity(detail.getAmount())
                    .product_code(detail.getProductDetail().getInfoProduct().getId()).build();

            list.add(item);
        }
        dataBody.put("order", order);
        dataBody.put("products", list);
        System.out.println(dataBody);
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(GHTKConfig.CREATE_ORDER_URL);
        StringEntity stringEntity = new StringEntity(dataBody.toString(), StandardCharsets.UTF_8);
        post.setHeader("content-type", "application/json");
        post.setHeader("Token", GHTKConfig.TOKEN);
        post.setEntity(stringEntity);
        CloseableHttpResponse res = client.execute(post);
        BufferedReader rd = new BufferedReader(new InputStreamReader(res.getEntity().getContent()));
        StringBuilder resultJsonStr = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            resultJsonStr.append(line);
        }
        JSONObject result = new JSONObject(resultJsonStr.toString());
        String message = result.get("message").toString();
        Boolean success = (Boolean) result.get("success");
        Map<String, Object> response = new HashMap<>();
        if (success == false) {
            response.put("message", message);
            response.put("success", success);
        } else {
            JSONObject data = (JSONObject) result.get("order");
            String order_code = data.get("label").toString();
            String partner_id = data.get("partner_id").toString();
            Integer fee = Integer.parseInt(data.get("fee").toString());
            Integer insurance_fee = Integer.parseInt(data.get("insurance_fee").toString());
            String estimated_pick_time = data.get("estimated_pick_time").toString();
            String estimated_deliver_time = data.get("estimated_deliver_time").toString();
            Integer status_id = data.getInt("status_id");
            response.put("message", message);
            response.put("success", success);
            response.put("order_code", order_code);
            response.put("partner_id", partner_id);
            response.put("fee", fee);
            response.put("insurance_fee", insurance_fee);
            response.put("estimated_pick_time", estimated_pick_time);
            response.put("estimated_deliver_time", estimated_deliver_time);
            response.put("status_id", status_id);
            od.getShipment().setShipCode(order_code);
            od.setFeeShip((fee+insurance_fee));
            orderRepository.save(od);
            shipmentRepository.save(od.getShipment());
        }
        return response;
    }

    @Transactional
    public ResponseEntity updateShipment(String token,Object GHTKResponse) {
        String ghtk_SecureHash =GHTKConfig.hmacSHA512(GHTKConfig.GHTK_HASHSECRET,GHTKConfig.TOKEN+GHTKConfig.SALT);
        System.out.println(ghtk_SecureHash);
        if (token.equals(ghtk_SecureHash)){

            JSONObject jsonObject = new JSONObject((Map) GHTKResponse);
            String partnerId = jsonObject.getString("partner_id");
            String label_id = jsonObject.getString("label_id");
            Integer status_id = jsonObject.getInt("status_id");
            String reason_code = jsonObject.getString("reason_code");
            String action_time = jsonObject.getString("action_time");
            String reason = jsonObject.getString("reason");
            if(status_id.equals(6)){
                //Don hang đã đc đối soát
                try {
                    Order order= orderRepository.findOrderById(Long.valueOf(partnerId));
                    Payment payment=Payment.builder().datePayment(action_time)
                            .bankName("GHTK").method(Epay.GHTK).order(order)
                            .status("00")
                            .tradingCode(label_id).build();
                    paymentRepository.save(payment);
                }catch (Exception e){
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                };
            }
            return ResponseEntity.status(200).body(ResponseEntity.status(200).body("Đơn hàng đã được cập nhật"));
        }
        else
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token không hợp lệ");
    }
}
