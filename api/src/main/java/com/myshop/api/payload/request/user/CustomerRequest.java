package com.myshop.api.payload.request.user;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
public class CustomerRequest {
    private String firstName;
    private String lastName;
    private String gender;

    @NotNull(message = "Email là bắt buộc")
    @Email(message = "Email sai định dạng")
    private String email;

    @NotNull(message = "Số điện thoại là bắt buộc")
    private String phone;

    @NotNull(message = "Mật khẩu là bắt buộc")
    private String password;

    private String address;
}
