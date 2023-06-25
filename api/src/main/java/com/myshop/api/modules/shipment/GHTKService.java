package com.myshop.api.service.shipment;

import com.myshop.api.payload.response.ship.ShipFeeResponse;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

public interface GHTKService {
    ShipFeeResponse calculateShipFee(String province, String district, Double weight, String deliver_option, Long value) throws URISyntaxException, IOException;

    Map<String, Object> createOrder(Long order_id) throws IOException;

    ResponseEntity updateShipment(String token, Object GHTKResponse);
}
