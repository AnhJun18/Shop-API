package com.myshop.api.payload.request.product;

import lombok.Data;

@Data
public class ProductPriceDetailRequest {
    private Long productId;
    private String newPrice;
    private String productName;
}
