package com.myshop.api.modules.stocks.stock_in;

import com.myshop.api.base.CRUDBaseServiceImpl;
import com.myshop.api.payload.request.product.AddProductDetailRequest;
import com.myshop.api.payload.response.product.ProductDetailResponse;
import com.myshop.repositories.stocks_in.entities.StocksIn;
import com.myshop.repositories.product.entities.Product;
import com.myshop.repositories.stocks.entities.StocksDetail;
import com.myshop.repositories.stocks.repos.StocksDetailRepository;
import com.myshop.repositories.product.repos.ProductRepository;
import com.myshop.repositories.stocks_in.entities.StocksInDetail;
import com.myshop.repositories.stocks_in.repos.StocksInDetailRepository;
import com.myshop.repositories.stocks_in.repos.StocksInRepository;
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
public class StockInServiceImpl extends CRUDBaseServiceImpl<StocksIn, StocksIn, StocksIn, Long> implements StockInService {

    private final StocksInRepository warehouseReceiptRepository;

    @Autowired
    StocksInDetailRepository warehouseReceiptDetailRepository;
    @Autowired
    StocksDetailRepository stocksDetailRepository;
    @Autowired
    ProductRepository productRepository;


    @Value("${jwkFile}")
    private Resource jwkFile;

    public StockInServiceImpl(StocksInRepository warehouseReceiptRepository) {
        super(StocksIn.class, StocksIn.class, StocksIn.class, warehouseReceiptRepository);
        this.warehouseReceiptRepository = warehouseReceiptRepository;
    }

    @Transactional
    @Override
    public ProductDetailResponse importWarehouse(List<AddProductDetailRequest> listRq) {
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
                StocksDetail tmp= null;
                if (productDetail.isPresent()) {
                    /*Nếu sản phẩm đã tồn tại size và color thì chỉ thêm số lượng*/
                    productDetail.get().setCurrent_number(productDetail.get().getCurrent_number() + productAddRq.getNumberAdd());
                    stocksDetailRepository.save(productDetail.get());
                    tmp=productDetail.get();
                } else {
                    /*Nếu sản phẩm chưa có size và color thì tạo chi tiết sản phẩm*/
                    StocksDetail newDetail = StocksDetail.builder()
                            .infoProduct(product.get()).size(productAddRq.getSize())
                            .color(productAddRq.getColor()).current_number(productAddRq.getNumberAdd()).build();
                    stocksDetailRepository.save(newDetail);
                    tmp=newDetail;
                }
                StocksInDetail newReceiptDetail= StocksInDetail.builder().stocksIn(newReceipt)
                        .stocksDetail(tmp).amount(productAddRq.getNumberAdd())
                        .costPrices(productAddRq.getPrices()).build();
                warehouseReceiptDetailRepository.save(newReceiptDetail);
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
    public Iterable<Map<String,Object>> getHistory(Date from, Date to) {

        return warehouseReceiptRepository.getAllReceipt(from.toInstant(),to.toInstant());
    }


}
