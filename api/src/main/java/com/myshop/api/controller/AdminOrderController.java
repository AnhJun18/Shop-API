package com.myshop.api.controller;

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
@RequestMapping("/api/admin-order")
public class AdminOrderController {

    @Autowired
    AuditorAware auditorProvider;

    @Autowired
    private OrderService orderService;

    @GetMapping("")
    public Mono<ApiResponse<?>> getListOrder(@RequestParam(required = false, defaultValue = "") String search,
                                             @RequestParam(required = false, defaultValue = "1") Integer page,
                                             @RequestParam(required = false, defaultValue = "10") Integer itemsPerPage,
                                             @RequestParam(required = false) Integer statusId,
                                             @RequestParam(required = false) String fromDate,
                                             @RequestParam(required = false) String toDate) {
        return Mono.just(orderService.getListOrder(search,statusId,fromDate,toDate,page,itemsPerPage));
    }

    @GetMapping("/status/get-options")
    public Mono<ApiResponse<?>> getOptsCategory() {
        return Mono.just(orderService.getOptionStatus());
    }

    @GetMapping("/{orderId}")
    public Mono<ApiResponse<?>> getDetailById(@PathVariable Long orderId) {
        return Mono.just(orderService.getDetailById(orderId));
    }

    @PostMapping("/{orderId}/confirm")
    public Mono<ApiResponse<?>> confirmOrder(@PathVariable Long orderId) {
        return Mono.just(orderService.confirmOrder(orderId,auditorProvider.getCurrentAuditor().get().toString()));
    }

    @PostMapping("/{orderId}/delivery")
    public Mono<ApiResponse<?>> addDeliveryOrder(@PathVariable Long orderId,@RequestBody String employeeDelivery) {
        return Mono.just(orderService.addDeliveryOrder(orderId,employeeDelivery));
    }

    @PostMapping("/{orderId}/done")
    public Mono<ApiResponse<?>> confirmDeliveryOrder(@PathVariable Long orderId) {
        return Mono.just(orderService.confirmDeliveryOrder(orderId));
    }
}