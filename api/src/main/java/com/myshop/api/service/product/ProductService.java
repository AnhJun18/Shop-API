package com.myshop.api.service.product;


import com.myshop.api.payload.request.product.ProductPriceRequest;
import com.myshop.api.payload.request.product.ProductRequest;
import com.myshop.common.http.ApiResponse;

import java.util.List;
import java.util.Map;

public interface ProductService {

    ApiResponse<?> createProduct(ProductRequest productRequest) ;
    ApiResponse<?> updateProduct(Long id,ProductRequest productRequest) ;
    ApiResponse<?> lockProduct(Long id) ;

    ApiResponse<?> getListProduct(String search, Integer page, Integer itemsPerPage, Long fromPrice, Long toPrice, List<String> types) ;

    ApiResponse<?> getById(Long id) ;

    ApiResponse<?> getDetailInventory(Long id) ;

    ApiResponse<?> updatePrice(String emailEmployee,ProductPriceRequest productPriceRequest);

    ApiResponse<?> getTopViewed() ;
    List<Map<String, Object>> getSuggest(String infoList) ;

    ApiResponse<?> getListProductOnPromotion() ;

    ApiResponse<?> getBestSelling() ;

    public ApiResponse<?> getOptionProduct();


}

