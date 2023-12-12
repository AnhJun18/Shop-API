package com.myshop.api.controller;


import com.myshop.api.payload.request.product.ProductRequest;
import com.myshop.api.service.product.ProductService;
import com.myshop.common.http.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

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
    ProductService productService;

    @GetMapping("")
    public Mono<ApiResponse<?>> getListCategory(@RequestParam(required = false, defaultValue = "") String search,
                                                @RequestParam(required = false, defaultValue = "1") Integer page,
                                                @RequestParam(required = false, defaultValue = "10") Integer itemsPerPage,
                                                @RequestParam(required = false) Long fromPrice,
                                                @RequestParam(required = false) Long toPrice,
                                                @RequestParam(required = false,name = "type[]") List<String> types ) {
        return Mono.just(productService.getListProduct(search, page, itemsPerPage, fromPrice, toPrice,types));
    }

    @PostMapping(value = "")
    public Mono<ApiResponse<?>> upload(@RequestBody ProductRequest productRequest) {
        return Mono.just(productService.createProduct(productRequest));
    }
    @PostMapping(value = "/update/{productID}")
    public Mono<ApiResponse<?>> upload(@PathVariable(name = "productID") Long id,@RequestBody ProductRequest productRequest) {
        return Mono.just(productService.updateProduct(id,productRequest));
    }

    @DeleteMapping(value = "/{productID}")
    public Mono<ApiResponse<?>> lockProduct(@PathVariable(name = "productID") Long id) {
        return Mono.just(productService.lockProduct(id));
    }

    @GetMapping("/{productID}")
    public Mono<ApiResponse<?>> getById(@PathVariable(name = "productID") Long id) {
        return Mono.just(productService.getById(id));
    }

    @GetMapping("/detail/{productID}")
    public Mono<ApiResponse<?>> getDetailInventory(@PathVariable(name = "productID") Long id) {
        return Mono.just(productService.getDetailInventory(id));
    }

    @GetMapping("/top-viewed")
    public Mono<ApiResponse<?>> getTopViewed() {
        return Mono.just(productService.getTopViewed());
    }

    @GetMapping("/best-selling")
    public Mono<ApiResponse<?>> getBestSelling() {
        return Mono.just(productService.getBestSelling());
    }

    @GetMapping("/list-promotion")
    public Mono<ApiResponse<?>> getListProductOnPromotion() {
        return Mono.just(productService.getListProductOnPromotion());
    }
    @GetMapping("/get-options")
    public Mono<ApiResponse<?>> getOptionProduct() {
        return Mono.just(productService.getOptionProduct());
    }

}
