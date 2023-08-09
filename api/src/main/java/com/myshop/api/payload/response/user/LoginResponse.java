package com.myshop.api.payload.response.user;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class LoginResponse {

    private boolean status;

    private String message;

    private String accessToken;

    private long expiresIn;

    private String refreshToken;

    private Object userInfo;

}
