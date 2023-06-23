package com.myshop.api.service.product;


import com.myshop.api.payload.request.product.AddProductDetailRequest;
import com.myshop.api.payload.request.product.ProductRequest;
import com.myshop.api.payload.response.product.ProductDetailResponse;
import com.myshop.api.payload.response.product.ProductResponse;
import com.myshop.repositories.product.entities.Product;
import com.myshop.repositories.product.entities.ProductDetail;
import org.springframework.data.domain.Page;
import org.springframework.http.codec.multipart.FilePart;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface ProductService {

    ProductResponse createProduct(ProductRequest productRequest, FilePart filePart) throws IOException;

    ProductResponse updateProduct(Long productID, ProductRequest productRequest, FilePart filePart) throws IOException;

    ProductResponse lockProduct(Long id);

    Product getProductById(Long id);

    ProductResponse unLockProduct(Long id);

    ProductDetailResponse createProductDetail(List<AddProductDetailRequest> detailRequest);

    Iterable<Product> getAllProduct();

    Iterable<Product> getProductBestSeller();

    Page<Map<String,Object>> getPagingProduct(Integer page, Integer size);

    Iterable<Product> searchByName( String name);

    Iterable<Product> getProductByCategory( String nameCategory);

    Iterable<ProductDetail> getDetailProductById(Long id_product);
}

