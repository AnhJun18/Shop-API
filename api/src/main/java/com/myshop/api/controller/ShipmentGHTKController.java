package com.myshop.api.controller;

import com.myshop.api.config.GHTKConfig;
import com.myshop.api.payload.response.ship.ItemProduct;
import com.myshop.api.payload.response.ship.ShipFeeResponse;
import com.myshop.api.service.order.OrderService;
import com.myshop.repositories.order.entities.Order;
import com.myshop.repositories.order.entities.OrderDetail;
import com.myshop.repositories.order.repos.OrderRepository;
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
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

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

@RestController
@RequestMapping(value = "/api/shipment/ghtk")
public class ShipmentGHTKController {

      @Autowired
    OrderService orderService;
    @Autowired
    OrderRepository orderRepository;

    @GetMapping(value = "/fee")
    public Mono<ShipFeeResponse> calculateShipFee(@RequestParam String province, @RequestParam String district,
                                                  @RequestParam Integer weight,
                                                  @RequestParam(name = "deliver_option", defaultValue = "none") String deliver_option,
                                                  @RequestParam Long value) throws IOException, URISyntaxException {
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
        return Mono.just(ship);
    }

    @PostMapping(value = "/create-order/{id}")
    public Map<String, Object> createOrder(@PathVariable(name = "id") Long order_id)
            throws  IOException {
        JSONObject dataBody = new JSONObject();
        Order od= orderRepository.findOrderById(order_id);
        JSONObject order = new JSONObject();
        order.put("id",od.getId().toString());
        order.put("pick_name", "Shop Ptit");
        order.put("pick_money", 100000);
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
        order.put("is_freeship","0");
        order.put("email",  od.getUserInfo().getAccount().getEmail());
        order.put("value", "100000");

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
        Map<String, Object> response = new HashMap<String, Object>();
        if (success == false) {
            response.put("message", message);
            response.put("success", success);
        } else {
            JSONObject data = (JSONObject) result.get("order");
            String order_code = data.get("label").toString();
            String partner_id = data.get("partner_id").toString();
            Long fee = Long.parseLong(data.get("fee").toString());
            Long insurance_fee = Long.parseLong(data.get("insurance_fee").toString());
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
        }
        return response;
    }


    @PostMapping(value = "/update-shipment")
    public Mono<ResponseEntity> update(@RequestParam ("hash") String token, @RequestBody Object GHTKresponse) {
        String ghtk_SecureHash =GHTKConfig.hmacSHA512(GHTKConfig.GHTK_HASHSECRET,GHTKConfig.TOKEN+GHTKConfig.SALT);
        JSONObject jsonObject = new JSONObject((Map) GHTKresponse);
        String partnerId = jsonObject.getString("partner_id");
        String label_id = jsonObject.getString("label_id");
        Integer status_id = jsonObject.getInt("status_id");
        String reason_code = jsonObject.getString("reason_code");
        String reason = jsonObject.getString("reason");
        System.out.println(partnerId+"-"+label_id+"-"+status_id);
        System.out.println(reason_code+" "+reason);

        if (token.equals(ghtk_SecureHash))
           return Mono.just(ResponseEntity.status(200).body(ResponseEntity.status(200).body("Đơn hàng đã được cập nhật")));
        else
            return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token không hợp lệ"));
    }

}