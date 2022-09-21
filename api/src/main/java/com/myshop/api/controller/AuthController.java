package com.myshop.api.controller;


import com.myshop.api.payload.request.user.LoginRequest;
import com.myshop.api.payload.request.user.UserRequest;
import com.myshop.api.payload.response.user.LoginResponse;
import com.myshop.api.payload.response.user.UserResponse;
import com.myshop.api.service.user.UserService;
import com.myshop.common.http.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

/**
 * @author Jun
 * @author  Jun
 */
@RestController
@RequestMapping("/api/auth/user")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Mono<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        LoginResponse resp = userService.login(loginRequest);
        return Mono.just(ApiResponse.of(resp));
    }




    @Transactional
    @PostMapping("/register")
    public Mono<ApiResponse<UserResponse>> registerRootStaff(@Valid @RequestBody UserRequest userRequest) {
        UserResponse resp = userService.registerUser(userRequest);
        return Mono.just(ApiResponse.of(resp));
    }

}
