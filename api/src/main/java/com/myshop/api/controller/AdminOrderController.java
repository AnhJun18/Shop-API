package com.myshop.api.controller;


import com.myshop.api.payload.response.order.OrderResponse;
import com.myshop.api.service.order.OrderService;
import com.myshop.repositories.order.entities.Order;
import com.myshop.security.jwt.CustomAuthUser;
import com.myshop.security.jwt.JWTAuthenticationToken;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.security.Principal;
import java.text.ParseException;
import java.util.Date;

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

    @PutMapping("/confirm/id={idOrder}")
    public Mono<OrderResponse> confirmOrder(Principal principal, @PathVariable("idOrder") Long id) {
        JWTAuthenticationToken jwtTokenObject = (JWTAuthenticationToken) principal;
        return Mono.just(orderService.confirmOrder(Long.parseLong(((CustomAuthUser) jwtTokenObject.getPrincipal()).getUserId()), id));
    }

    @PutMapping("/delivery/id={idOrder}")
    public Mono<OrderResponse> deliveryOrder( @PathVariable("idOrder") Long id) {
        return Mono.just(orderService.deliveryOrder(id));
    }

    @PutMapping("/paid_order/id={idOrder}")
    public Mono<OrderResponse> confirmPaidOrder( @PathVariable("idOrder") Long id) {
        return Mono.just(orderService.confirmPaidOrder(id));
    }

    @DeleteMapping("/cancel_order/id={idOrder}")
    public Mono<OrderResponse> confirmCancelOrder( @PathVariable("idOrder") Long id) {
        return Mono.just(orderService.confirmCancelOrder(id));
    }

    @GetMapping("/search")
    public Mono<Page<Order>> getTheOrderByStatus(@RequestParam(name = "from", required = false, defaultValue = "2022-01-01") @DateTimeFormat(pattern = "yyyy-MM-dd") Date fromDate,
                                                 @RequestParam(name = "to", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date toDate,
                                                 @RequestParam(name = "status", required = false) String status,
                                                 @RequestParam(name = "info", required = false) String info,
                                                 @RequestParam(name = "page", defaultValue = "1", required = false) Integer page,
                                                 @RequestParam(name = "size", defaultValue = "10", required = false) Integer size) throws ParseException {

        return Mono.just(orderService.searchOrder(fromDate, toDate == null ? new Date() : toDate, info, status, page, size));
    }


}
