package com.myshop.api.service.product;


import com.myshop.api.payload.request.product.CategoryRequest;
import com.myshop.repositories.product.entities.Category;

public interface CategoryService {

    Category createCategory(CategoryRequest categoryRequest);

    Iterable<Category> getAllCategory();

}
