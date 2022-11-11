package com.myshop.api.service.product;


import com.myshop.api.payload.request.product.ProductDetailRequest;
import com.myshop.api.payload.request.product.ProductRequest;
import com.myshop.api.payload.response.product.ProductDetailResponse;
import com.myshop.api.payload.response.product.ProductResponse;
import com.myshop.repositories.product.entities.Product;
import org.springframework.data.domain.Page;
import org.springframework.http.codec.multipart.FilePart;

import java.io.IOException;

public interface ProductService {

    ProductResponse createProduct(ProductRequest productRequest, FilePart filePart) throws IOException;

    ProductDetailResponse createProductDetail(ProductDetailRequest detailRequest);

    Iterable<Product> getAllProduct();

    Page<Product> getPagingProduct(Integer page, Integer size);

    Iterable<Product> getProductByCategory( String nameCategory);
}

