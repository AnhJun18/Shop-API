package com.myshop.api.controller;


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
@RequestMapping("/api/shopping_cart")
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;

    @PostMapping()
    public Mono<ApiResponse<Object>> addToCart(Principal principal,
                                               @RequestParam("product_id") Long productID,
                                               @RequestParam("amount") Long amount) {
        JWTAuthenticationToken jwtTokenObject = (JWTAuthenticationToken) principal;
        String userID = ((CustomAuthUser) jwtTokenObject.getPrincipal()).getUserId();
        return Mono.just(shoppingCartService.addToCart(Long.parseLong(userID), productID, amount));
    }
}

