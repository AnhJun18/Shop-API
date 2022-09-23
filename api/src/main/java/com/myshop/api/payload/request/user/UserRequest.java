package com.myshop.api.payload.request.user;

import lombok.Data;

@Data
public class UserRequest {
    private String userName;
    private String firstName;
    private String lastName;
    private String gender;
    private String email;
    private String phone;
    private String roleName;
    private String password;
    private String address;
}
