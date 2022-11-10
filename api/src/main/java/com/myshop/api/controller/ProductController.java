package com.myshop.api.controller;


import com.myshop.api.payload.request.product.CategoryRequest;
import com.myshop.api.payload.request.product.ProductDetailRequest;
import com.myshop.api.payload.request.product.ProductRequest;
import com.myshop.api.payload.response.product.ProductDetailResponse;
import com.myshop.api.payload.response.product.ProductResponse;
import com.myshop.api.service.product.CategoryService;
import com.myshop.api.service.product.ProductService;
import com.myshop.repositories.product.entities.Category;
import com.myshop.repositories.product.entities.Product;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.io.IOException;

/**
 * @author Anh Jun
 * @author Anh Jun
 */

@Slf4j
@RestController
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/api/product")
public class ProductController {

    @Autowired
    CategoryService categoryService;

    @Autowired
    ProductService productService;

    @PostMapping("/category")
    public Mono<Category> createCategory(@RequestBody CategoryRequest categoryRequest) {
        return Mono.just(categoryService.createCategory(categoryRequest));
    }

    @GetMapping("/category/all")
    public Mono<Iterable<Category>> getAllCategory() {
        return Mono.just(categoryService.getAllCategory());
    }

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ProductResponse> upload(@RequestParam("name") String name,
                                        @RequestParam("category") Long category,
                                        @RequestParam("describe") String describe,
                                        @RequestParam("price") Double price,
                                        @RequestPart("image") FilePart filePart) throws IOException {
        ProductRequest product = ProductRequest.builder().name(name)
                .category(category).image(filePart)
                .describe(describe).price(price).build();
        return Mono.just(productService.createProduct(product, filePart));
    }

    @PostMapping(value = "/detail")
    public Mono<ProductDetailResponse> createDetailProduct(@RequestBody ProductDetailRequest detailRequest) throws IOException {
        return Mono.just(productService.createProductDetail(detailRequest));
    }

    @GetMapping("/all")
    public Mono<Iterable<Product>> getAllProduct(){
        return Mono.just(productService.getAllProduct());
    }

    @GetMapping("/get_paging")
    public Mono<Page<Product>> getPaging(
            @RequestParam(name = "page", defaultValue = "1", required = false) Integer page,
            @RequestParam(name = "size", defaultValue = "10", required = false) Integer size) {
        return Mono.just(productService.getPagingProduct(page, size));
    }
}
