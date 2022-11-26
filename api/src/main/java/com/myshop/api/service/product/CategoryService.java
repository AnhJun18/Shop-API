package com.myshop.api.service.product;


import com.myshop.api.payload.request.product.CategoryRequest;
import com.myshop.common.http.ApiResponse;
import com.myshop.repositories.product.entities.Category;

public interface CategoryService {

    ApiResponse<?> createCategory(CategoryRequest categoryRequest);

    Iterable<Category> getAllCategory();

    ApiResponse<Object> updateCategory(Long id, CategoryRequest categoryRequest);

    ApiResponse<?> deleteCategory(Long id);

}
