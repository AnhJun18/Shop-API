package com.myshop.api.payload.request.user;

import lombok.Data;

@Data
public class ForgotPasswordRequest {
    private String email;
}
