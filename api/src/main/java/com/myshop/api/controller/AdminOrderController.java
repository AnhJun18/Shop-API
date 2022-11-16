package com.myshop.api.controller;


import com.myshop.api.payload.response.order.OrderResponse;
import com.myshop.api.service.order.OrderService;
import com.myshop.repositories.order.entities.Order;
import com.myshop.security.jwt.CustomAuthUser;
import com.myshop.security.jwt.JWTAuthenticationToken;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.security.Principal;

/**
 * @author Anh Jun
 * @author Anh Jun
 */

@Slf4j
@RestController
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/api/admin/order")
public class AdminOrderController {


    @Autowired
    private OrderService orderService;

    @GetMapping("/all")
    public Mono<Iterable<Order>> getAllOrder() {
        return Mono.just(orderService.getAllOrderByAdmin());
    }

    @GetMapping("/status={statusName}")
    public Mono<Iterable<Order>> getTheOrderByStatus(@PathVariable("statusName") String status) {
        return Mono.just(orderService.getOrderByStatusByAdmin(status));
    }

    @PutMapping("/confirm/id={idOrder}&status={statusName}")
    public Mono<OrderResponse> confirmOrder(Principal principal, @PathVariable("statusName") String status, @PathVariable("idOrder") Long id) {
        JWTAuthenticationToken jwtTokenObject = (JWTAuthenticationToken) principal;
        return Mono.just(orderService.confirmOrder(Long.parseLong(((CustomAuthUser) jwtTokenObject.getPrincipal()).getUserId()),id,status));
    }


}
