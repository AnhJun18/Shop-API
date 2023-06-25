package com.myshop.api.service.user;


import com.myshop.api.payload.request.user.*;
import com.myshop.api.payload.response.user.LoginResponse;
import com.myshop.api.payload.response.user.PasswordResponse;
import com.myshop.api.payload.response.user.UserResponse;
import com.myshop.common.http.ApiResponse;
import com.myshop.repositories.user.entities.UserInfo;

public interface UserService {

    LoginResponse login(LoginRequest loginRequest);

    LoginResponse loginWithGoogle(String code_verify);

    UserResponse registerUser(UserRequest userRequest);

    UserResponse getUserProfile(long userId);

    LoginResponse refreshToken(String refreshToken);

    ApiResponse<Object> deleteAccountUser(long userID);

    ApiResponse<Object>  updateProfile(Long userID, UpdateProfileRequest profileRequest) ;

    PasswordResponse forgotPassword(ForgotPasswordRequest forgotPasswordRequest);

    PasswordResponse verifyCode(String codeRequest);

    PasswordResponse resetPassword(ResetPasswordRequest resetPasswordRequest);
    
    Iterable<UserInfo> getAllUser();

}
