package com.myshop.api.service.product;


import com.myshop.api.payload.request.product.CategoryRequest;
import com.myshop.common.http.ApiResponse;

public interface CategoryService {

    ApiResponse<?> createCategory(CategoryRequest categoryRequest) ;
    ApiResponse<?> getListCategory(String search,Integer page, Integer itemsPerPage) ;
    ApiResponse<?> getById(String code) ;
    ApiResponse<?> getOptionCategory();
}
