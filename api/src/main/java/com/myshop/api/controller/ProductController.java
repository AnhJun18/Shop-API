package com.myshop.api.controller;


import com.myshop.api.payload.request.product.AddProductDetailRequest;
import com.myshop.api.payload.request.product.ProductRequest;
import com.myshop.api.payload.response.product.ProductDetailResponse;
import com.myshop.api.payload.response.product.ProductResponse;
import com.myshop.api.service.product.CategoryService;
import com.myshop.api.service.product.ProductService;
import com.myshop.common.http.ApiResponse;
import com.myshop.repositories.product.entities.Product;
import com.myshop.repositories.product.entities.ProductDetail;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;
import java.util.Map;

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


    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ProductResponse> upload(@RequestParam("name") String name,
                                        @RequestParam("category") String category,
                                        @RequestParam(value = "describe",required = false) String describe,
                                        @RequestParam("price") Double price,
                                        @RequestPart(value = "tags",required = false) String tag,
                                        @RequestPart("image") FilePart filePart) throws IOException {
        ProductRequest product = ProductRequest.builder().name(name)
                .category(category).image(filePart).tag(tag)
                .describe(describe).price(price).build();
        return Mono.just(productService.createProduct(product, filePart));
    }

    @PutMapping(value = "/edit/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ProductResponse> updateProduct(@RequestParam(value = "name",required = false) String name,
                                               @RequestParam(value = "category",required = false) String category,
                                               @RequestParam(value = "describe",required = false) String describe,
                                               @RequestParam(value = "price",required = false) Double price,
                                               @RequestPart(value = "image",required = false) FilePart filePart,
                                               @PathVariable String id) throws IOException {
        ProductRequest product = ProductRequest.builder().name(name)
                .category(category).image(filePart)
                .describe(describe).price(price).build();
        return Mono.just(productService.updateProduct(Long.valueOf(id),product, filePart));
    }

    @DeleteMapping(value = "/block")
    public Mono<ProductResponse> lockProduct(@RequestParam Long productID){
        return Mono.just(productService.lockProduct(productID));
    }

    @DeleteMapping(value = "/un_block")
    public Mono<ProductResponse> unLockProduct(@RequestParam Long productID){
        return Mono.just(productService.unLockProduct(productID));
    }

    @PostMapping(value = "/detail")
    public Mono<ProductDetailResponse> createDetailProduct(@RequestBody List<AddProductDetailRequest> detailRequest) throws IOException {
        return Mono.just(productService.createProductDetail(detailRequest));
    }

    @GetMapping(value = "/detail/{id_product}")
    public Mono<Iterable<ProductDetail>> getDetailProductById(@PathVariable Long id_product) throws IOException {
        return Mono.just(productService.getDetailProductById(id_product));
    }

    @GetMapping("/all")
    public Mono<Iterable<Product>> getAllProduct(){
        return Mono.just(productService.getAllProduct());
    }

    @GetMapping("/get_paging")
    public Mono<Page<Map<String,Object>>> getPaging(
            @RequestParam(name = "page", defaultValue = "1", required = false) Integer page,
            @RequestParam(name = "size", defaultValue = "10", required = false) Integer size) {
        return Mono.just(productService.getPagingProduct(page, size));
    }

    @GetMapping("/search")
    public Mono<Iterable<Product>> search(@RequestParam(required = false)String name) {
        return Mono.just(productService.searchByName(name));
    }

    @GetMapping("/category={nameCategory}")
    public Mono<Iterable<Product>> getProductByCategory(@PathVariable String nameCategory) {
        return Mono.just(productService.getProductByCategory(nameCategory));
    }

    @GetMapping("/tags")
    public Mono<Iterable<Product>> getProductByTag(@RequestParam ("nameTag") String tag) {
        return Mono.just(productService.getProductByTag(tag));
    }


}
