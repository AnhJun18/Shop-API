package com.myshop.api.controller;


import com.myshop.api.payload.request.user.CustomerRequest;
import com.myshop.api.payload.request.user.ForgotPasswordRequest;
import com.myshop.api.payload.request.user.LoginRequest;
import com.myshop.api.payload.request.user.RefreshRequest;
import com.myshop.api.payload.request.user.ResetPasswordRequest;
import com.myshop.api.payload.response.user.LoginResponse;
import com.myshop.api.payload.response.user.PasswordResponse;
import com.myshop.api.payload.response.user.UserResponse;
import com.myshop.api.service.user.UserService;
import com.myshop.common.http.ApiResponse;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Anh Jun
 * @author Anh Jun
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

  @Autowired
  private UserService userService;


  @PostMapping("/login")
  public Mono<ApiResponse<?>> login(@Valid @RequestBody LoginRequest loginRequest) {
    LoginResponse resp = userService.login(loginRequest);
    if (resp.isStatus())
      return Mono.just(ApiResponse.of(resp));
    else
      return Mono.just(ApiResponse.builder().status(401).message(resp.getMessage()).build());

  }

  @PostMapping("/register")
  public Mono<ApiResponse<UserResponse>> registerForCustomer(@Valid @RequestBody CustomerRequest customerRequest) {
    UserResponse resp = userService.registerUser(customerRequest);
    return Mono.just(ApiResponse.of(resp));
  }

  //
  @PostMapping("/refresh-token")
  public Mono<ApiResponse<LoginResponse>> refreshToken(@RequestBody RefreshRequest request) {
    return Mono.just(ApiResponse.of(userService.refreshToken(request.getRefreshToken())));
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
