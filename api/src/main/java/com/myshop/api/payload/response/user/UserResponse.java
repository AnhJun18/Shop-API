package com.myshop.api.payload.response.user;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserResponse {
    private boolean status;
    private String message;
    private Object data;
}
