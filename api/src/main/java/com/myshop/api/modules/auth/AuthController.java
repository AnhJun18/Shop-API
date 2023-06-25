package com.myshop.api.modules.auth;


import com.myshop.api.payload.request.user.ForgotPasswordRequest;
import com.myshop.api.payload.request.user.LoginRequest;
import com.myshop.api.payload.request.user.ResetPasswordRequest;
import com.myshop.api.payload.request.user.UserRequest;
import com.myshop.api.payload.response.user.LoginResponse;
import com.myshop.api.payload.response.user.PasswordResponse;
import com.myshop.api.payload.response.user.UserResponse;
import com.myshop.api.modules.user.UserService;
import com.myshop.common.http.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

/**
 * @author Anh Jun
 * @author  Anh Jun
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

    @GetMapping("/refresh/{refresh-token}")
    public Mono<ApiResponse<LoginResponse>> refreshToken(@PathVariable("refresh-token") String refreshToken) {
        return Mono.just(ApiResponse.of(userService.refreshToken(refreshToken)));
    }


    @Transactional
    @PostMapping("/forgot-password")
    public Mono<ApiResponse<PasswordResponse>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        PasswordResponse resp = userService.forgotPassword(forgotPasswordRequest);
        return Mono.just(ApiResponse.of(resp));
    }

    @GetMapping("/verify-code/{code}")
    public Mono<PasswordResponse> verifyCode(@PathVariable("code") String codeRequest) {
        PasswordResponse resp = userService.verifyCode(codeRequest);
        return Mono.just(resp);
    }

    @Transactional
    @PostMapping("/reset-password")
    public Mono<ApiResponse<PasswordResponse>> forgotPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {
        PasswordResponse resp = userService.resetPassword(resetPasswordRequest);
        return Mono.just(ApiResponse.of(resp));
    }

}
