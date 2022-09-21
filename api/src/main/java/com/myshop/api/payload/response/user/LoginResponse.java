package com.myshop.api.payload.response.user;

import com.myshop.repositories.user.entities.User;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class LoginResponse {

    private String accessToken;
    @Default
    private String tokenType = "bearer";

    private long expiresIn;

    private boolean status;

    private String refreshToken;

    private String message;

    private String unblockMessage;

    private int errorCode;

    private int errorCount;

    private long unblockTime;

    private User user;

    private List<AbilityResponse> ability;

}
