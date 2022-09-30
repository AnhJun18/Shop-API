package com.myshop.api.payload.response.product;

import com.myshop.repositories.product.entities.ProductDetail;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ProductDetailResponse {
    private boolean status;
    private String message;
    private ProductDetail productDetail;
}
