package com.myshop.api.payload.response.product;

import com.myshop.repositories.product.entities.Product;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ProductResponse {
    private boolean status;
    private String message;
    private Product product;
}
