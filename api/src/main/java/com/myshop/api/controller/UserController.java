package com.myshop.api.controller;


import com.myshop.api.payload.request.user.UpdateProfileRequest;
import com.myshop.api.payload.response.user.UserResponse;
import com.myshop.api.service.user.UserService;
import com.myshop.common.http.ApiResponse;
import com.myshop.repositories.user.entities.Customer;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;
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
  @Autowired
  AuditorAware<String> auditorAware;

  @GetMapping("/profile")
  public Mono<ApiResponse<UserResponse>> getUserCurrent(Principal principal) {
    return Mono.just(ApiResponse.of(userService.getUserProfile(auditorAware.getCurrentAuditor().get())));
  }

  @GetMapping("/employee/get-options")
  public Mono<ApiResponse<?>> getOptsEmployee() {
    return Mono.just(userService.getOptsEmployee());
  }

  //
  @PutMapping("/profile")
  public Mono<ApiResponse<?>> updateProfile(@RequestBody UpdateProfileRequest profileRequest) {
    return Mono.just(userService.updateProfile(auditorAware.getCurrentAuditor().get(), profileRequest));

  }

  //
//    @DeleteMapping("")
//    public Mono<ApiResponse<?>> blockUserCurrent(Principal principal) {
//            JWTAuthenticationToken jwtTokenObject = (JWTAuthenticationToken) principal;
//            return Mono.just(userService.deleteAccountUser(Long.parseLong(((CustomAuthUser) jwtTokenObject.getPrincipal()).getUserId())));
//
//    }
//
  @GetMapping("/all")
  public ApiResponse<?> getUsers() {
    return ApiResponse.of(userService.getUsers());
  }


}
