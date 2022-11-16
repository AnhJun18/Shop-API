package com.myshop.api.controller;


import com.myshop.api.payload.request.user.UpdateProfileRequest;
import com.myshop.api.payload.response.user.UserResponse;
import com.myshop.api.service.user.UserService;
import com.myshop.common.http.ApiResponse;
import com.myshop.repositories.user.entities.UserInfo;
import com.myshop.security.jwt.CustomAuthUser;
import com.myshop.security.jwt.JWTAuthenticationToken;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
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

    @PutMapping("/profile")
    public Mono<ApiResponse<?>>  updateProfile (Principal principal, @RequestBody UpdateProfileRequest profileRequest) {
        JWTAuthenticationToken jwtTokenObject = (JWTAuthenticationToken) principal;
        return Mono.just(userService.updateProfile(Long.parseLong(((CustomAuthUser) jwtTokenObject.getPrincipal()).getUserId()),profileRequest));

    }

    @DeleteMapping("")
    public Mono<ApiResponse<?>> blockUserCurrent(Principal principal) {
            JWTAuthenticationToken jwtTokenObject = (JWTAuthenticationToken) principal;
            return Mono.just(userService.deleteAccountUser(Long.parseLong(((CustomAuthUser) jwtTokenObject.getPrincipal()).getUserId())));

    }

    @GetMapping("/all")
    public Iterable<UserInfo> getUsers(){
        return userService.getAll();
    }


}
