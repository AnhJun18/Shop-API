package com.myshop.api.service.user;


import com.myshop.api.payload.request.user.CustomerRequest;
import com.myshop.api.payload.request.user.ForgotPasswordRequest;
import com.myshop.api.payload.request.user.LoginRequest;
import com.myshop.api.payload.request.user.ResetPasswordRequest;
import com.myshop.api.payload.request.user.UpdateProfileRequest;
import com.myshop.api.payload.response.user.LoginResponse;
import com.myshop.api.payload.response.user.PasswordResponse;
import com.myshop.api.payload.response.user.UserResponse;
import com.myshop.common.http.ApiResponse;
import com.myshop.repositories.user.entities.Customer;

public interface UserService {

    LoginResponse login(LoginRequest loginRequest);

//    LoginResponse loginWithGoogle(String code_verify);
//
    UserResponse registerUser(CustomerRequest customerRequest);
//
    UserResponse getUserProfile(String email);
//
    LoginResponse refreshToken(String refreshToken);

    ApiResponse<?> getOptsEmployee();
//
//    ApiResponse<Object> deleteAccountUser(long userID);
//
    ApiResponse<Object>  updateProfile(String userID, UpdateProfileRequest profileRequest) ;
//
    PasswordResponse forgotPassword(ForgotPasswordRequest forgotPasswordRequest);
//
    PasswordResponse verifyCode(String codeRequest);

    PasswordResponse resetPassword(ResetPasswordRequest resetPasswordRequest);
//
//    Iterable<UserInfo> getAllUser();

    Iterable<Customer> getUsers();

}
