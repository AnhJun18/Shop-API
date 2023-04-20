package com.myshop.api.controller;

import com.myshop.api.service.payment.VnPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping(value = "/api/payment/vnpay")
public class PaymentVnPayController {
    @Autowired
    VnPayService vnpayService;

    @GetMapping("/make_url")
    public ResponseEntity createPayment(ServerHttpRequest request, @RequestParam(name = "order_id") Long order_id) throws UnsupportedEncodingException {
        String url_payment = vnpayService.createPayment(request, order_id);
        return ResponseEntity.status(302).header(HttpHeaders.LOCATION, url_payment).build();
    }


    @GetMapping(value = "/result/VnPayIPN")
    public ResponseEntity resultPayment(ServerHttpRequest request) throws UnsupportedEncodingException {
        String response_url = vnpayService.resultPayment(request);
        return ResponseEntity.status(302).header(HttpHeaders.LOCATION, response_url).build();
    }

}