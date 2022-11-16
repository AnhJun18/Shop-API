package com.myshop.api.controller;


import com.myshop.api.payload.request.shopping_cart.ShoppingCartRequest;
import com.myshop.api.service.shopping_cart.ShoppingCartService;
import com.myshop.common.http.ApiResponse;
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
@RequestMapping("/api/cart")
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;

    @PostMapping("/AddToCart")
    public Mono<ApiResponse<Object>> addToCart(Principal principal,
                                               @RequestBody ShoppingCartRequest item) {
        JWTAuthenticationToken jwtTokenObject = (JWTAuthenticationToken) principal;
        String userID = ((CustomAuthUser) jwtTokenObject.getPrincipal()).getUserId();
        return Mono.just(shoppingCartService.addToCart(Long.parseLong(userID),item));
    }


    @PutMapping("/update")
    public Mono<ApiResponse<Object>> updateCart(Principal principal,
                                               @RequestBody ShoppingCartRequest item) {
        JWTAuthenticationToken jwtTokenObject = (JWTAuthenticationToken) principal;
        String userID = ((CustomAuthUser) jwtTokenObject.getPrincipal()).getUserId();
        return Mono.just(shoppingCartService.updateCart(Long.parseLong(userID),item));
    }

    @GetMapping()
    public Mono<Iterable<Object>> getShoppingCart(Principal principal) {
        JWTAuthenticationToken jwtTokenObject = (JWTAuthenticationToken) principal;
        String userID = ((CustomAuthUser) jwtTokenObject.getPrincipal()).getUserId();
        return Mono.just(shoppingCartService.getShoppingCart(Long.parseLong(userID)));
    }


}

