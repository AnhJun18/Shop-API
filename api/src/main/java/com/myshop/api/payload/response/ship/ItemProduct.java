package com.myshop.api.payload.response.ship;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ItemProduct {
    private String name;
    private Long quantity;
    private Integer price;
    private Double weight;
    private Long product_code;
}
