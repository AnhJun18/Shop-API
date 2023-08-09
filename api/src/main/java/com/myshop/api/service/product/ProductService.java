package com.myshop.api.service.product;


import com.myshop.api.payload.request.product.ProductRequest;
import com.myshop.common.http.ApiResponse;

import java.util.List;

public interface ProductService {

    ApiResponse<?> createProduct(ProductRequest productRequest) ;

    ApiResponse<?> getListProduct(String search, Integer page, Integer itemsPerPage, Long fromPrice, Long toPrice, List<String> types) ;

    ApiResponse<?> getById(Long id) ;

    ApiResponse<?> getDetailInventory(Long id) ;

    ApiResponse<?> getTopViewed() ;

    ApiResponse<?> getListProductOnPromotion() ;

    ApiResponse<?> getBestSelling() ;


}

