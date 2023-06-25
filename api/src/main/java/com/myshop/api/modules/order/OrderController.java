package com.myshop.api.modules.order;


import com.myshop.api.payload.request.order.OrderRequest;
import com.myshop.api.payload.response.order.OrderResponse;
import com.myshop.repositories.order.entities.Order;
import com.myshop.security.jwt.CustomAuthUser;
import com.myshop.security.jwt.JWTAuthenticationToken;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.security.Principal;
import java.util.List;

/**
 * @author Anh Jun
 * @author Anh Jun
 */

@Slf4j
@RestController
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/api/order")
public class OrderController {


    @Autowired
    private OrderService orderService;

    @PostMapping("/create")
    public Mono<OrderResponse> order(Principal principal,@RequestBody OrderRequest orderRequest) {

        JWTAuthenticationToken jwtTokenObject = (JWTAuthenticationToken) principal;
        return Mono.just(orderService.order(Long.parseLong(((CustomAuthUser) jwtTokenObject.getPrincipal()).getUserId()),orderRequest));
    }

    @GetMapping("/all")
    public Mono<List<Order>> getTheOrder(Principal principal) {
        JWTAuthenticationToken jwtTokenObject = (JWTAuthenticationToken) principal;
        return Mono.just(orderService.getTheOrder(Long.parseLong(((CustomAuthUser) jwtTokenObject.getPrincipal()).getUserId())));
    }

    @GetMapping("/status={statusName}")
    public Mono<Iterable<Order>> getTheOrderByStatus(Principal principal, @PathVariable("statusName") String status) {
        JWTAuthenticationToken jwtTokenObject = (JWTAuthenticationToken) principal;
        return Mono.just(orderService.getTheOrderByStatus(Long.parseLong(((CustomAuthUser) jwtTokenObject.getPrincipal()).getUserId()),status));
    }

    @DeleteMapping("/cancel_order")
    public Mono<OrderResponse> confirmCancelOrder(@RequestParam("order_id") Long id) {
        return Mono.just(orderService.cancelOrderByUser(id));
    }

}
