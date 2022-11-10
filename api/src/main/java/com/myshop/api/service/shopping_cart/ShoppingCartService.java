package com.myshop.api.service.shopping_cart;

import com.myshop.api.payload.request.shopping_cart.ShoppingCartRequest;
import com.myshop.common.http.ApiResponse;

public interface ShoppingCartService {


   ApiResponse<Object> addToCart(Long userId, ShoppingCartRequest item);

   Iterable<Object> getShoppingCart(Long userID);
}
