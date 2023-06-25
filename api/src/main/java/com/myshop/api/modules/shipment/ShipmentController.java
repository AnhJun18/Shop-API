package com.myshop.api.controller;

import com.myshop.api.payload.response.ship.ShipFeeResponse;
import com.myshop.api.service.shipment.GHTKService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/shipment/ghtk")
public class ShipmentController {
    @Autowired
    GHTKService ghtkService;

    @GetMapping(value = "/fee")
    public Mono<ShipFeeResponse> calculateShipFee(@RequestParam String province, @RequestParam String district,
                                                  @RequestParam Double weight,
                                                  @RequestParam(name = "deliver_option", defaultValue = "none") String deliver_option,
                                                  @RequestParam Long value) throws IOException, URISyntaxException {
        return Mono.just(ghtkService.calculateShipFee(province,district,weight,deliver_option,value));
    }

    @PostMapping(value = "/create-order/{id}")
    public Mono<Map<String, Object>> createOrder(@PathVariable(name = "id") Long order_id)
            throws  IOException {
        return Mono.just(ghtkService.createOrder(order_id));

    }

    @PostMapping(value = "/update-shipment")
    public Mono<ResponseEntity> update(@RequestParam ("hash") String token, @RequestBody Object GHTKResponse) {
            return Mono.just(ghtkService.updateShipment(token,GHTKResponse));
    }

}