package com.myshop.api.payload.request.user;

import lombok.Data;

import javax.persistence.Column;

@Data
public class UpdateProfileRequest {
    private String firstName;
    private String lastName;
    private String gender;
    private String phone;
    private String province;
    private String district;
    private String ward;
    private String address;
}
