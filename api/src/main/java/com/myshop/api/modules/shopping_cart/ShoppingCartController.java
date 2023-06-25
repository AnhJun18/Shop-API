package com.myshop.api.modules.shopping_cart;


import com.myshop.api.payload.request.shopping_cart.ShoppingCartRequest;
import com.myshop.common.http.ApiResponse;
import com.myshop.security.jwt.CustomAuthUser;
import com.myshop.security.jwt.JWTAuthenticationToken;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.security.Principal;
import java.util.Map;

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

    @DeleteMapping("{id}")
    public Mono<ApiResponse<Object>> updateCart(Principal principal, @PathVariable("id") String idProduct) {
        JWTAuthenticationToken jwtTokenObject = (JWTAuthenticationToken) principal;
        String userID = ((CustomAuthUser) jwtTokenObject.getPrincipal()).getUserId();
        return Mono.just(shoppingCartService.deleteItem(Long.parseLong(userID), Long.valueOf(idProduct)));
    }

    @GetMapping("/all")
    public Mono<Iterable<Map<String,Object>>> getShoppingCart(Principal principal) {
        JWTAuthenticationToken jwtTokenObject = (JWTAuthenticationToken) principal;
        String userID = ((CustomAuthUser) jwtTokenObject.getPrincipal()).getUserId();
        return Mono.just(shoppingCartService.getShoppingCart(Long.parseLong(userID)));
    }


}

