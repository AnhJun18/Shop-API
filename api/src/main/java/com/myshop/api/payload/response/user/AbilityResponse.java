package com.myshop.api.payload.response.user;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class AbilityResponse {
    private String action;
    private String subject;
}
