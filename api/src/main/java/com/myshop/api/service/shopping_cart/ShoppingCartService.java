package com.myshop.api.service.shopping_cart;

import com.myshop.common.http.ApiResponse;

public interface ShoppingCartService {


   ApiResponse<Object> addToCart(Long userId, Long productID, Long amount);
}
