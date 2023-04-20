package com.myshop.api.service.payment;

import org.springframework.http.server.reactive.ServerHttpRequest;

import java.io.UnsupportedEncodingException;

public interface VnPayService {
    String createPayment(ServerHttpRequest request, Long order_id) throws UnsupportedEncodingException;

    String resultPayment(ServerHttpRequest request) throws UnsupportedEncodingException;
}
