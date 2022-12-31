package com.myshop.api.service.product;

import com.myshop.api.base.CRUDBaseServiceImpl;
import com.myshop.api.payload.request.product.CategoryRequest;
import com.myshop.common.http.ApiResponse;
import com.myshop.repositories.product.entities.Category;
import com.myshop.repositories.product.repos.CategoryRepository;
import com.myshop.repositories.product.repos.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Transactional
@Service
public class CategoryServiceImpl extends CRUDBaseServiceImpl<Category, CategoryRequest, Category, Long> implements CategoryService {

    private final CategoryRepository categoryRepository;
    @Autowired
    ProductRepository productRepository;

    @Value("${jwkFile}")
    private Resource jwkFile;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        super(Category.class, CategoryRequest.class, Category.class, categoryRepository);
        this.categoryRepository = categoryRepository;
    }

    @Override
    public ApiResponse<?> createCategory(CategoryRequest categoryRequest) {
        if(categoryRepository.existsByName(categoryRequest.getName()))
            return ApiResponse.builder().status(100).data(null).message("Danh mục đã tồn tại").build();
        Category newCategory = Category.builder().name(categoryRequest.getName()).build();
        categoryRepository.save(newCategory);
        return ApiResponse.builder().status(200).data(newCategory).message("Danh mục đã được thêm").build();
    }

    @Override
    public Iterable<Category> getAllCategory() {
        return categoryRepository.findAll();
    }

    @Override
    public ApiResponse<Object> updateCategory(Long id, CategoryRequest categoryRequest) {
        Optional<Category> category = categoryRepository.findById(id);
        if (category.isPresent()) {
            category.get().setName(categoryRequest.getName());
            categoryRepository.save(category.get());
            return ApiResponse.builder().status(200).data(category).message("Cập nhật thành công").build();
        } else {
            return ApiResponse.builder().status(0).data(null).message("Danh mục không tồn tại").build();
        }
    }

    @Override
    public ApiResponse<?> deleteCategory(Long id) {

        Optional<Category> category = categoryRepository.findById(id);
        if (category.isPresent()) {
            if (productRepository.findAllByCategory_Name(category.get().getName()).iterator().hasNext())
                return ApiResponse.builder().status(0).data(category).message("Danh mục đã kinh doanh không thể xóa").build();
            categoryRepository.delete(category.get());
            return ApiResponse.builder().status(200).data(category).message("Cập nhật thành công").build();
        } else {
            return ApiResponse.builder().status(0).data(null).message("Danh mục không tồn tại").build();
        }
    }
}
