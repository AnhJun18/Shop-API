package com.myshop.api.service.product;

import com.myshop.api.base.CRUDBaseServiceImpl;
import com.myshop.api.payload.request.product.ProductDetailRequest;
import com.myshop.api.payload.request.product.ProductRequest;
import com.myshop.api.payload.response.product.ProductDetailResponse;
import com.myshop.api.payload.response.product.ProductResponse;
import com.myshop.api.service.firebase.IImageService;
import com.myshop.repositories.product.entities.Category;
import com.myshop.repositories.product.entities.Product;
import com.myshop.repositories.product.entities.ProductDetail;
import com.myshop.repositories.product.repos.CategoryRepository;
import com.myshop.repositories.product.repos.ProductDetailRepository;
import com.myshop.repositories.product.repos.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Optional;


@Transactional
@Service
public class ProductServiceImpl extends CRUDBaseServiceImpl<Product, ProductRequest, Product, Long> implements ProductService {

    private final ProductRepository productRepository;

    @Autowired
    ProductDetailRepository productDetailRepository;
    @Autowired
    IImageService imageService;
    @Autowired
    CategoryRepository categoryRepository;

    @Value("${jwkFile}")
    private Resource jwkFile;

    public ProductServiceImpl(ProductRepository productRepository) {
        super(Product.class, ProductRequest.class, Product.class, productRepository);
        this.productRepository = productRepository;
    }

    @Transactional
    @Override
    public ProductResponse createProduct(ProductRequest productRequest, FilePart fileImage) throws IOException {
        Optional<Category> category = categoryRepository.findById(productRequest.getCategory());
        if (!category.isPresent()) {
            return ProductResponse.builder().message("Category is not exists").status(false).build();
        }
        Product product = Product.builder()
                .name(productRequest.getName())
                .category(category.get())
                .linkImg(imageService.save(fileImage))
                .describe(productRequest.getDescribe())
                .price(productRequest.getPrice()).sold(0L).build();
        productRepository.save(product);
        return ProductResponse.builder().message("Create Product Successful").status(true).product(product).build();
    }

    @Override
    public ProductDetailResponse createProductDetail(ProductDetailRequest productDetailRq) {

        Optional<Product> product = productRepository.findById(productDetailRq.getProduct_id());
        if (!product.isPresent()) {
            return ProductDetailResponse.builder().message("Product is not exists").status(false).build();
        }
        ProductDetail newDetail = ProductDetail.builder()
                .product(product.get()).size(productDetailRq.getSize())
                .color(productDetailRq.getColor()).current_number(productDetailRq.getCurrent_number()).build();
        productDetailRepository.save(newDetail);
        return ProductDetailResponse.builder().status(true).message("Create Product Detail Successful")
                .productDetail(newDetail).build();
    }

    @Override
    public Iterable<Product> getAllProduct() {

        return productRepository.findAll();
    }

    @Override
    public Page<Product> getPagingProduct(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        return productRepository.findAll(pageable);
    }

    @Override
    public Iterable<Product> getProductByCategory(String nameCategory) {
        return productRepository.findAllByCategory_Name(nameCategory);
    }


}
