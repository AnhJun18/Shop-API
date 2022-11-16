package com.myshop.api.service.shopping_cart;

import com.myshop.api.payload.request.shopping_cart.ShoppingCartRequest;
import com.myshop.common.http.ApiResponse;

public interface ShoppingCartService {


   ApiResponse<Object> addToCart(Long userId, ShoppingCartRequest item);

   ApiResponse<Object> updateCart(Long userId, ShoppingCartRequest item);

   ApiResponse<Object> deleteItem(Long userId, Long productID);

   Iterable<Object> getShoppingCart(Long userID);
}
