package com.myshop.api.payload.request.shopping_cart;

import lombok.Data;

@Data
public class ShoppingCartRequest {
    private Long productDetailID;
    private Long quantity;
}
