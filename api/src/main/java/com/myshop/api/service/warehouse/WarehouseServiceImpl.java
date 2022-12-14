package com.myshop.api.service.warehouse;

import com.myshop.api.base.CRUDBaseServiceImpl;
import com.myshop.api.payload.request.product.AddProductDetailRequest;
import com.myshop.api.payload.response.product.ProductDetailResponse;
import com.myshop.repositories.warehouse.entities.WarehouseReceipt;
import com.myshop.repositories.product.entities.Product;
import com.myshop.repositories.product.entities.ProductDetail;
import com.myshop.repositories.product.repos.ProductDetailRepository;
import com.myshop.repositories.product.repos.ProductRepository;
import com.myshop.repositories.warehouse.entities.WarehouseReceiptDetail;
import com.myshop.repositories.warehouse.repos.WarehouseReceiptDetailRepository;
import com.myshop.repositories.warehouse.repos.WarehouseReceiptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Transactional
@Service
public class WarehouseServiceImpl extends CRUDBaseServiceImpl<WarehouseReceipt, WarehouseReceipt, WarehouseReceipt, Long> implements WarehouseService {

    private final WarehouseReceiptRepository warehouseReceiptRepository;

    @Autowired
    WarehouseReceiptDetailRepository warehouseReceiptDetailRepository;
    @Autowired
    ProductDetailRepository productDetailRepository;
    @Autowired
    ProductRepository productRepository;


    @Value("${jwkFile}")
    private Resource jwkFile;

    public WarehouseServiceImpl(WarehouseReceiptRepository warehouseReceiptRepository) {
        super(WarehouseReceipt.class, WarehouseReceipt.class, WarehouseReceipt.class, warehouseReceiptRepository);
        this.warehouseReceiptRepository = warehouseReceiptRepository;
    }

    @Transactional
    @Override
    public ProductDetailResponse importWarehouse(List<AddProductDetailRequest> listRq) {
        String message = "";
        boolean result = false;

        try {
            WarehouseReceipt newReceipt=WarehouseReceipt.builder().build();
            warehouseReceiptRepository.save(newReceipt);
            for (AddProductDetailRequest productAddRq : listRq) {
                Optional<Product> product = productRepository.findById(productAddRq.getProduct_id());
                if (!product.isPresent()) {
                    throw new Exception("Kh??ng t??m th???y s???n ph???m c???n th??m");
                }
                Optional<ProductDetail> productDetail = productDetailRepository.findProductDetailByInfoProduct_IdAndSizeAndAndColor(productAddRq.getProduct_id(), productAddRq.getSize(), productAddRq.getColor());
                ProductDetail tmp= null;
                if (productDetail.isPresent()) {
                    /*N???u s???n ph???m ???? t???n t???i size v?? color th?? ch??? th??m s??? l?????ng*/
                    productDetail.get().setCurrent_number(productDetail.get().getCurrent_number() + productAddRq.getNumberAdd());
                    productDetailRepository.save(productDetail.get());
                    tmp=productDetail.get();
                } else {
                    /*N???u s???n ph???m ch??a c?? size v?? color th?? t???o chi ti???t s???n ph???m*/
                    ProductDetail newDetail = ProductDetail.builder()
                            .infoProduct(product.get()).size(productAddRq.getSize())
                            .color(productAddRq.getColor()).current_number(productAddRq.getNumberAdd()).build();
                    productDetailRepository.save(newDetail);
                    tmp=newDetail;
                }
                WarehouseReceiptDetail newReceiptDetail=WarehouseReceiptDetail.builder().warehouseReceipt(newReceipt)
                        .productDetail(tmp).amount(productAddRq.getNumberAdd())
                        .costPrices(productAddRq.getPrices()).build();
                warehouseReceiptDetailRepository.save(newReceiptDetail);
            }
            result = true;
            message = "Nh???p h??ng th??nh c??ng";
        } catch (Exception e) {
            message = "L???i nh???p h??ng! " + e.getMessage();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }

        return ProductDetailResponse.builder().status(result).message(message).build();
    }

    @Override
    public Iterable<Map<String,Object>> getHistory(Date from, Date to) {

        return warehouseReceiptRepository.getAllReceipt(from.toInstant(),to.toInstant());
    }


}
