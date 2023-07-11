package com.myshop.api.payload.response.user;

import com.myshop.repositories.user.entities.UserInfo;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserResponse {
    private boolean status;
    private String message;
    private UserInfo data;

}
