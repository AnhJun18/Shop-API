package com.myshop.api.controller;


import com.myshop.api.payload.request.product.CategoryRequest;
import com.myshop.api.service.product.CategoryService;
import com.myshop.common.http.ApiResponse;
import com.myshop.repositories.product.entities.Category;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * @author Anh Jun
 * @author Anh Jun
 */

@Slf4j
@RestController
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/api/category")
public class CategoryController {

    @Autowired
    CategoryService categoryService;


    @PostMapping("")
    public Mono<ApiResponse<?>> createCategory(@RequestBody CategoryRequest categoryRequest) {
        return Mono.just(categoryService.createCategory(categoryRequest));
    }

    @GetMapping("/all")
    public Mono<Iterable<Category>> getAllCategory() {
        return Mono.just(categoryService.getAllCategory());
    }


    @PutMapping ("/update/{id}")
    public Mono<ApiResponse<?>> updateCategory(@PathVariable Long id,@RequestBody CategoryRequest categoryRequest) {
        return Mono.just(categoryService.updateCategory(id,categoryRequest));
    }

    @DeleteMapping("/delete/{id}")
    public Mono<ApiResponse<?>> deleteCategory(@PathVariable Long id) {
        return Mono.just(categoryService.deleteCategory(id));
    }



}
