package com.myshop.api.controller;


import com.myshop.api.payload.request.shopping_cart.ShoppingCartRequest;
import com.myshop.api.service.shopping_cart.ShoppingCartService;
import com.myshop.common.http.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

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

    @Autowired
    AuditorAware auditorProvider;

    @PostMapping("/update")
    public Mono<ApiResponse<?>> addToCart(@RequestBody ShoppingCartRequest item) {
        return Mono.just(shoppingCartService.addToCart(auditorProvider.getCurrentAuditor().get().toString(),item));
    }


//    @PutMapping("/update")
//    public Mono<ApiResponse<Object>> updateCart(Principal principal,
//                                               @RequestBody ShoppingCartRequest item) {
//        JWTAuthenticationToken jwtTokenObject = (JWTAuthenticationToken) principal;
//        String userID = ((CustomAuthUser) jwtTokenObject.getPrincipal()).getUserId();
//        return Mono.just(shoppingCartService.updateCart(Long.parseLong(userID),item));
//    }
//
//    @DeleteMapping("{id}")
//    public Mono<ApiResponse<Object>> updateCart(Principal principal, @PathVariable("id") String idProduct) {
//        JWTAuthenticationToken jwtTokenObject = (JWTAuthenticationToken) principal;
//        String userID = ((CustomAuthUser) jwtTokenObject.getPrincipal()).getUserId();
//        return Mono.just(shoppingCartService.deleteItem(Long.parseLong(userID), Long.valueOf(idProduct)));
//    }
//
    @GetMapping("/all")
    public ApiResponse<?> getShoppingCart() {
        return shoppingCartService.getShoppingCart(auditorProvider.getCurrentAuditor().get().toString());
    }


}

