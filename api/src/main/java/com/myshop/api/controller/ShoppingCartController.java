package com.myshop.api.controller;


import com.myshop.api.payload.request.shopping_cart.ShoppingCartRequest;
import com.myshop.api.service.shopping_cart.ShoppingCartService;
import com.myshop.common.http.ApiResponse;
import com.myshop.security.jwt.CustomAuthUser;
import com.myshop.security.jwt.JWTAuthenticationToken;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.security.Principal;

/**
 * @author Anh Jun
 * @author Anh Jun
 */

@Slf4j
@RestController
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/api/shopping_cart")
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;

    @PostMapping()
    public Mono<ApiResponse<Object>> addToCart(Principal principal,
                                               @RequestBody ShoppingCartRequest item) {
        JWTAuthenticationToken jwtTokenObject = (JWTAuthenticationToken) principal;
        String userID = ((CustomAuthUser) jwtTokenObject.getPrincipal()).getUserId();
        return Mono.just(shoppingCartService.addToCart(Long.parseLong(userID),item));
    }
}

