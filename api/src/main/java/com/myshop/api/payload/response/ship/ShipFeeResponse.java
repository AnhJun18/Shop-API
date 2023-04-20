package com.myshop.api.payload.response.ship;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ShipFeeResponse {
    private Long only_ship_fee;
    private Long total_ship_fee;
    private Long insurance_fee;
    private String message;
    private Boolean success;

}
