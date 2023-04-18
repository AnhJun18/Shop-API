package com.myshop.api.controller;

import com.myshop.api.config.GHTKConfig;
import com.myshop.api.payload.response.ship.ShipFeeResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/shipment/ghtk")
public class ShipmentGHTKController {


    // tính phí ship
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

    @GetMapping(value = "/update_shipment")
    public Mono<ResponseEntity> update(@RequestParam ("hash") String token) {
        String ghtk_SecureHash =GHTKConfig.hmacSHA512(GHTKConfig.GHTK_HASHSECRET,GHTKConfig.TOKEN+GHTKConfig.SALT);
        if (token == ghtk_SecureHash)
           return Mono.just(ResponseEntity.status(200).body(ResponseEntity.status(200).body("Đơn hàng đã được cập nhật")));
        else
            return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token không hợp lệ"));

    }


}