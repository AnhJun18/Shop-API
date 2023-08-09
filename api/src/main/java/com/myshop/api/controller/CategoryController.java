package com.myshop.api.controller;


import com.myshop.api.payload.request.product.CategoryRequest;
import com.myshop.api.service.product.CategoryService;
import com.myshop.common.http.ApiResponse;
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

    @GetMapping("")
    public Mono<ApiResponse<?>> getListCategory(@RequestParam(required = false,defaultValue = "") String search,
                                                @RequestParam(required = false,defaultValue = "1") Integer page,
                                                @RequestParam(required = false,defaultValue = "10") Integer itemsPerPage) {
        return Mono.just(categoryService.getListCategory(search,page,itemsPerPage));
    }

    @GetMapping("/{code}")
    public Mono<ApiResponse<?>> getById(@PathVariable(name = "code")String code) {
        return Mono.just(categoryService.getById(code));
    }

    @GetMapping("/get-options")
    public Mono<ApiResponse<?>> getOptsCategory() {
        return Mono.just(categoryService.getOptionCategory());
    }
}
