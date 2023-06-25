package com.myshop.api.modules.product;

import com.myshop.api.base.CRUDBaseServiceImpl;
import com.myshop.api.payload.request.product.AddProductDetailRequest;
import com.myshop.api.payload.request.product.ProductRequest;
import com.myshop.api.payload.response.product.ProductDetailResponse;
import com.myshop.api.payload.response.product.ProductResponse;
import com.myshop.api.service.firebase.IImageService;
import com.myshop.repositories.product.builder.ProductBuilder;
import com.myshop.repositories.product.entities.Category;
import com.myshop.repositories.product.entities.Product;
import com.myshop.repositories.stocks.entities.StocksDetail;
import com.myshop.repositories.product.repos.CategoryRepository;
import com.myshop.repositories.stocks.repos.StocksDetailRepository;
import com.myshop.repositories.product.repos.ProductRepository;
import com.myshop.repositories.stocks_in.entities.StocksIn;
import com.myshop.repositories.stocks_in.entities.StocksInDetail;
import com.myshop.repositories.stocks_in.repos.StocksInDetailRepository;
import com.myshop.repositories.stocks_in.repos.StocksInRepository;
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
    StocksDetailRepository stocksDetailRepository;
    @Autowired
    IImageService imageService;
    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    StocksInRepository warehouseReceiptRepository;
    @Autowired
    StocksInDetailRepository warehouseReceiptDetailRepository;

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
        if (productRepository.existsByName(productRequest.getName())) {
            return ProductResponse.builder().message("Tên sản phẩm đã tồn tại!").status(false).build();
        }

        Product product = new ProductBuilder()
                .setName(productRequest.getName())
                .setDescribe(productRequest.getDescribe())
                .setCategory(category)
                .setLinkImg(imageService.save(fileImage))
                .setPrice(productRequest.getPrice()).setSold(0L)
                .setDeleteFlag(false).build();

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

    @Override
    public ProductResponse lockProduct(Long id) {
        Optional<Product> product = productRepository.findById(id);
        if (!product.isPresent())
            return ProductResponse.builder().status(false).message("Cannot find product").build();
        product.get().setDeleteFlag(true);
        productRepository.save(product.get());
        return ProductResponse.builder().status(true).message("Block product successful").product(product.get()).build();
    }

    @Override
    public Product getProductById(Long id) {
        if(productRepository.findById(id).isPresent())
            return productRepository.findById(id).get();
        else
            return null;

    }

    @Override
    public ProductResponse unLockProduct(Long id) {
        Optional<Product> product = productRepository.findById(id);
        if (!product.isPresent())
            return ProductResponse.builder().status(false).message("Cannot find product").build();
        product.get().setDeleteFlag(false);
        productRepository.save(product.get());
        return ProductResponse.builder().status(true).message("Unblock product successful").product(product.get()).build();
    }


    @Transactional
    @Override
    public ProductDetailResponse createProductDetail(List<AddProductDetailRequest> listRq) {
        String message = "";
        boolean result = false;

        try {
            StocksIn newReceipt= StocksIn.builder().build();
            warehouseReceiptRepository.save(newReceipt);
            for (AddProductDetailRequest productAddRq : listRq) {
                Optional<Product> product = productRepository.findById(productAddRq.getProduct_id());
                if (!product.isPresent()) {
                    throw new Exception("Không tìm thấy sản phẩm cần thêm");
                }
                Optional<StocksDetail> productDetail = stocksDetailRepository.findProductDetailByInfoProduct_IdAndSizeAndAndColor(productAddRq.getProduct_id(), productAddRq.getSize(), productAddRq.getColor());
                StocksInDetail newReceiptDetail= StocksInDetail.builder().stocksIn(newReceipt)
                        .stocksDetail(productDetail.get()).amount(productAddRq.getNumberAdd())
                        .costPrices(productAddRq.getPrices()).build();
                warehouseReceiptDetailRepository.save(newReceiptDetail);
                if (productDetail.isPresent()) {
                    /*Nếu sản phẩm đã tồn tại size và color thì chỉ thêm số lượng*/
                    productDetail.get().setCurrent_number(productDetail.get().getCurrent_number() + productAddRq.getNumberAdd());
                    stocksDetailRepository.save(productDetail.get());
                } else {
                    /*Nếu sản phẩm chưa có size và color thì tạo chi tiết sản phẩm*/
                    StocksDetail newDetail = StocksDetail.builder()
                            .infoProduct(product.get()).size(productAddRq.getSize())
                            .color(productAddRq.getColor()).current_number(productAddRq.getNumberAdd()).build();
                    stocksDetailRepository.save(newDetail);
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
    public Iterable<Map<String,Object>> getAllProduct() {
        return productRepository.findAllSortCategory();
    }

    @Override
    public Iterable<Map<String,Object>> getProductBestSeller() {
        return productRepository.getProductBestSeller();
    }

    @Override
    public Page<Map<String, Object>> getPagingProduct(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("category.name").ascending());
        return productRepository.getListProductPaging(pageable);
    }

    @Override
    public Iterable<Product> searchByName(String name) {
        return productRepository.searchProductByName(name);
    }

    @Override
    public Iterable<Product> getProductByCategory(String nameCategory) {
        return productRepository.findAllByCategory_NameAndDeleteFlag(nameCategory,false);
    }

    @Override
    public Iterable<Map<String,Object>> getDetailProductById(Long id_product) {

        return stocksDetailRepository.findAllByInfoProduct_Id(id_product);
    }


}
