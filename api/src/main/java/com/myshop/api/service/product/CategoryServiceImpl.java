package com.myshop.api.service.product;

import com.myshop.api.base.CRUDBaseServiceImpl;
import com.myshop.api.payload.request.product.CategoryRequest;
import com.myshop.repositories.product.entities.Category;
import com.myshop.repositories.product.repos.CategoryRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Transactional
@Service
public class CategoryServiceImpl extends CRUDBaseServiceImpl<Category, CategoryRequest, Category, Long> implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Value("${jwkFile}")
    private Resource jwkFile;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        super(Category.class, CategoryRequest.class, Category.class, categoryRepository);
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Category createCategory(CategoryRequest categoryRequest) {

        Category newCategory = Category.builder().name(categoryRequest.getName()).build();
        categoryRepository.save(newCategory);
         return newCategory;
    }
}
