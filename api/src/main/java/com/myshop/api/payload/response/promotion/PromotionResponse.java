package com.myshop.api.payload.response.promotion;

import com.myshop.repositories.product.entities.Product;
import com.myshop.repositories.promotion.entity.Promotion;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class PromotionResponse {
    private boolean status;
    private String message;
    private Promotion promotion;
}
