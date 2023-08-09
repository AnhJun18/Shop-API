package com.myshop.api.controller;

import com.myshop.api.payload.request.order.OrderRequest;
import com.myshop.api.payload.response.order.OrderResponse;
import com.myshop.api.service.order.OrderService;
import com.myshop.common.http.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    AuditorAware auditorProvider;

    @Autowired
    private OrderService orderService;

    @PostMapping("")
    public Mono<OrderResponse> order(@RequestBody OrderRequest orderRequest) {

        return Mono.just(orderService.order(auditorProvider.getCurrentAuditor().get().toString(),orderRequest));
    }

    @GetMapping("/state/{orderId}")
    public Mono<ApiResponse<?>> order(@PathVariable("orderId") String orderId) {
        return Mono.just(orderService.getStateOrder(Long.parseLong(orderId)));
    }

    @GetMapping("/history/{statusName}")
    public Mono<ApiResponse<?>> getOrderByStatus(@PathVariable("statusName") String statusName) {
        return Mono.just(orderService.getHistoryOrderByStatus(auditorProvider.getCurrentAuditor().get().toString(),statusName));
    }

    @DeleteMapping("/{orderId}")
    public Mono<ApiResponse<?>> cancelOrderById(@PathVariable("orderId") Long orderId) {
        return Mono.just(orderService.cancelOrderById(auditorProvider.getCurrentAuditor().get().toString(),orderId));
    }


}