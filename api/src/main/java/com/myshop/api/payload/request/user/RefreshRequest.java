package com.myshop.api.payload.request.user;

import lombok.Data;

@Data
public class RefreshRequest {
    private String refreshToken;
}
