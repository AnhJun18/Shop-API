package com.myshop.api.payload.response.user;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class PasswordResponse {
    private boolean status;
    private String message;
    private int errorCode;
}
