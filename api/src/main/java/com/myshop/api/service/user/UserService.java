package com.myshop.api.service.user;


import com.myshop.api.payload.request.user.LoginRequest;
import com.myshop.api.payload.request.user.UserRequest;
import com.myshop.api.payload.response.user.LoginResponse;
import com.myshop.api.payload.response.user.UserResponse;

public interface UserService {

    LoginResponse login(LoginRequest loginRequest);

    UserResponse registerUser(UserRequest userRequest);

}
