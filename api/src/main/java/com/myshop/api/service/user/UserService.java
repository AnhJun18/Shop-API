package com.myshop.api.service.user;


import com.myshop.api.payload.request.user.LoginRequest;
import com.myshop.api.payload.request.user.UserRequest;
import com.myshop.api.payload.response.user.LoginResponse;
import com.myshop.api.payload.response.user.UserResponse;
import com.myshop.common.http.ApiResponse;
import com.myshop.repositories.user.entities.Account;

public interface UserService {

    LoginResponse login(LoginRequest loginRequest);

    UserResponse registerUser(UserRequest userRequest);

    UserResponse getUserProfile(long userId);

    LoginResponse refreshToken(String refreshToken);

    ApiResponse<Object> deleteAccountUser(long userID);
    
    Iterable<Account> getAll();

}
