package com.myshop.api.payload.request.user;

import lombok.Data;

@Data
public class UpdateProfileRequest {
    private String firstName;
    private String lastName;
    private String gender;
    private String phone;
}
