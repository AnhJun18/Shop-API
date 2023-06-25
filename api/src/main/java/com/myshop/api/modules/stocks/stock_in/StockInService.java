package com.myshop.api.modules.stocks.stock_in;

import com.myshop.api.payload.request.product.AddProductDetailRequest;
import com.myshop.api.payload.response.product.ProductDetailResponse;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface StockInService {

    ProductDetailResponse importWarehouse(List<AddProductDetailRequest> detailRequest);

    Iterable<Map<String,Object>> getHistory(Date from, Date to);
}

