package com.myshop.api.payload.response.user;

import com.myshop.repositories.user.entities.Role;
import com.myshop.repositories.user.entities.User;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserResponse {
    private boolean status;
    private String message;
    private User user;
    private Role role;
}
