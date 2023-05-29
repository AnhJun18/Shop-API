package com.myshop.api.payload.request.promotion;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class AddPrToPromotionRequest {
    private Long promotionID;
    private List<Long> listProductID;

}
