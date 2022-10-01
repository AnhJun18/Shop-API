package com.myshop.api.controller;


import com.myshop.api.payload.response.user.UserResponse;
import com.myshop.api.service.user.UserService;
import com.myshop.common.http.ApiResponse;
import com.myshop.repositories.user.entities.Account;
import com.myshop.repositories.user.entities.User;
import com.myshop.security.jwt.CustomAuthUser;
import com.myshop.security.jwt.JWTAuthenticationToken;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.security.Principal;

/**
 * @author Anh Jun
 * @author Anh Jun
 */
@RestController
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    public Mono<ApiResponse<UserResponse>> getUserCurrent(final Mono<Principal> principal) {
        return principal.map(jwtToken -> {
            JWTAuthenticationToken jwtTokenObject = (JWTAuthenticationToken) jwtToken;
            return userService.getUserProfile(Long.parseLong(((CustomAuthUser) jwtTokenObject.getPrincipal()).getUserId()));
        }).map(ApiResponse::new);
    }

    @DeleteMapping("")
    public Mono<ApiResponse<?>> blockUserCurrent(Principal principal) {
            JWTAuthenticationToken jwtTokenObject = (JWTAuthenticationToken) principal;
            return Mono.just(userService.deleteAccountUser(Long.parseLong(((CustomAuthUser) jwtTokenObject.getPrincipal()).getUserId())));

    }

    @GetMapping("/all")
    public Iterable<Account> getUsers(){
        return userService.getAll();
    }


}
