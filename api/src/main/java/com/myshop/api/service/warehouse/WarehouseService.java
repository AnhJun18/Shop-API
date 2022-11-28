package com.myshop.api.service.warehouse;

import com.myshop.api.payload.request.product.AddProductDetailRequest;
import com.myshop.api.payload.response.product.ProductDetailResponse;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface WarehouseService {

    ProductDetailResponse importWarehouse(List<AddProductDetailRequest> detailRequest);

    Iterable<Map<String,Object>> getHistory(Date from, Date to);
}

