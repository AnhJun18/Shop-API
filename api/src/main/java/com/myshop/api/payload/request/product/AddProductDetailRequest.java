package com.myshop.api.payload.request.product;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddProductDetailRequest {
    private Long product_id;
    private String size;
    private String color;
    private Long numberAdd;
    private Integer prices;
}
