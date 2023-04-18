package com.myshop.api.payload.request.user;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
public class UserRequest {
    private String userName;
    private String firstName;
    private String lastName;
    private String gender;

    @NotNull(message = "Email cannot be null")
    @Email(message = "Email sai be null")
    private String email;

    @NotNull(message = "Phone cannot be null")
    @NotNull
    private String phone;

    private String roleName;
    private String password;
}
