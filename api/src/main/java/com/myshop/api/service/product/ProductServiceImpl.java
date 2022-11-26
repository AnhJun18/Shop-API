package com.myshop.api.service.product;

import com.myshop.api.base.CRUDBaseServiceImpl;
import com.myshop.api.payload.request.product.AddProductDetailRequest;
import com.myshop.api.payload.request.product.ProductRequest;
import com.myshop.api.payload.response.product.ProductDetailResponse;
import com.myshop.api.payload.response.product.ProductResponse;
import com.myshop.api.service.firebase.IImageService;
import com.myshop.repositories.order.entities.WarehouseReceipt;
import com.myshop.repositories.product.entities.Category;
import com.myshop.repositories.product.entities.Product;
import com.myshop.repositories.product.entities.ProductDetail;
import com.myshop.repositories.product.repos.CategoryRepository;
import com.myshop.repositories.product.repos.ProductDetailRepository;
import com.myshop.repositories.product.repos.ProductRepository;
import com.myshop.repositories.warehouse.entity.WarehouseReceiptDetail;
import com.myshop.repositories.warehouse.repos.WarehouseReceiptDetailRepository;
import com.myshop.repositories.warehouse.repos.WarehouseReceiptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.io.IOException;
import java.util.List;
import java.util.Map;
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
    @Autowired
    WarehouseReceiptRepository warehouseReceiptRepository;
    @Autowired
    WarehouseReceiptDetailRepository warehouseReceiptDetailRepository;

    @Value("${jwkFile}")
    private Resource jwkFile;

    public ProductServiceImpl(ProductRepository productRepository) {
        super(Product.class, ProductRequest.class, Product.class, productRepository);
        this.productRepository = productRepository;
    }

    @Transactional
    @Override
    public ProductResponse createProduct(ProductRequest productRequest, FilePart fileImage) throws IOException {
        Category category = categoryRepository.findByName(productRequest.getCategory());
        if (category == null) {
            return ProductResponse.builder().message("Category is not exists").status(false).build();
        }
        Product product = Product.builder()
                .name(productRequest.getName())
                .category(category)
                .linkImg(imageService.save(fileImage))
                .describe(productRequest.getDescribe())
                .price(productRequest.getPrice()).sold(0L).build();
        productRepository.save(product);
        return ProductResponse.builder().message("Create Product Successful").status(true).product(product).build();
    }

    @Override
    public ProductResponse updateProduct(Long productID, ProductRequest productRequest, FilePart filePart) throws IOException {
        Optional<Product> product = productRepository.findById(productID);
        if (!product.isPresent())
            return ProductResponse.builder().status(false).message("Cannot find product").build();
        if (productRequest.getDescribe() != null)
            product.get().setDescribe(productRequest.getDescribe());

        if (productRequest.getPrice() != null)
            product.get().setPrice(productRequest.getPrice());

        if (productRequest.getName() != null)
            product.get().setName(productRequest.getName());

        if (productRequest.getCategory() != null)
            product.get().setCategory(categoryRepository.findByName(productRequest.getCategory()));
        productRepository.save(product.get());
        return ProductResponse.builder().status(true).message("Update product successful").product(product.get()).build();
    }


    @Transactional
    @Override
    public ProductDetailResponse createProductDetail(List<AddProductDetailRequest> listRq) {
        String message = "";
        boolean result = false;

        try {
            WarehouseReceipt newReceipt=WarehouseReceipt.builder().build();
            warehouseReceiptRepository.save(newReceipt);
            for (AddProductDetailRequest productAddRq : listRq) {
                Optional<Product> product = productRepository.findById(productAddRq.getProduct_id());
                if (!product.isPresent()) {
                    throw new Exception("Không tìm thấy sản phẩm cần thêm");
                }
                Optional<ProductDetail> productDetail = productDetailRepository.findProductDetailByInfoProduct_IdAndSizeAndAndColor(productAddRq.getProduct_id(), productAddRq.getSize(), productAddRq.getColor());
                WarehouseReceiptDetail newReceiptDetail=WarehouseReceiptDetail.builder().warehouseReceipt(newReceipt)
                        .productDetail(productDetail.get()).amount(productAddRq.getNumberAdd())
                        .costPrices(productAddRq.getPrices()).build();
                warehouseReceiptDetailRepository.save(newReceiptDetail);
                if (productDetail.isPresent()) {
                    /*Nếu sản phẩm đã tồn tại size và color thì chỉ thêm số lượng*/
                    productDetail.get().setCurrent_number(productDetail.get().getCurrent_number() + productAddRq.getNumberAdd());
                    productDetailRepository.save(productDetail.get());
                } else {
                    /*Nếu sản phẩm chưa có size và color thì tạo chi tiết sản phẩm*/
                    ProductDetail newDetail = ProductDetail.builder()
                            .infoProduct(product.get()).size(productAddRq.getSize())
                            .color(productAddRq.getColor()).current_number(productAddRq.getNumberAdd()).build();
                    productDetailRepository.save(newDetail);
                }
            }
            result = true;
            message = "Nhập hàng thành công";
        } catch (Exception e) {
            message = "Lỗi nhập hàng! " + e.getMessage();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }

        return ProductDetailResponse.builder().status(result).message(message).build();
    }

    @Override
    public Iterable<Product> getAllProduct() {
        return productRepository.findAllSortCategory();
    }

    @Override
    public Page<Map<String, Object>> getPagingProduct(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("category.name").ascending());
        return productRepository.getListProductPaging(pageable);
    }

    @Override
    public Iterable<Product> getProductByCategory(String nameCategory) {
        return productRepository.findAllByCategory_Name(nameCategory);
    }

    @Override
    public Iterable<ProductDetail> getDetailProductById(Long id_product) {

        return productDetailRepository.findAllByInfoProduct_Id(id_product);
    }


}
