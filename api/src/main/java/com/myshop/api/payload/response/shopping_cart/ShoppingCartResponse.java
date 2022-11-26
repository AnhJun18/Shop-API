package com.myshop.api.payload.response.shopping_cart;

import com.myshop.repositories.product.entities.Product;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ShoppingCartResponse {
    private boolean status;
    private String message;
    private Product product;
}
