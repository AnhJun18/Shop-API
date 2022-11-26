package com.myshop.api.service.warehouse;

import com.myshop.api.payload.request.product.AddProductDetailRequest;
import com.myshop.api.payload.response.product.ProductDetailResponse;
import com.myshop.repositories.order.entities.WarehouseReceipt;

import java.util.List;

public interface WarehouseService {

    ProductDetailResponse importWarehouse(List<AddProductDetailRequest> detailRequest);

    List<WarehouseReceipt> getHistory();
}

