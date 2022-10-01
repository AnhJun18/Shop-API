package com.myshop.api.payload.request.product;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductDetailRequest {
    private Long product_id;
    private int size;
    private String color;
    private Long current_number;
}
