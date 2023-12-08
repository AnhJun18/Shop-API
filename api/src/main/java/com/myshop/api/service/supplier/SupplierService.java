package com.myshop.api.service.supplier;


import com.myshop.api.payload.request.stockin.RequestSupplierRequest;
import com.myshop.api.payload.request.system.ColorRequest;
import com.myshop.api.payload.request.system.SizeRequest;
import com.myshop.common.http.ApiResponse;

import java.io.ByteArrayOutputStream;

public interface SupplierService {

    ByteArrayOutputStream exportExcelRequestSupplier(RequestSupplierRequest exportRequest)throws Exception;
    ApiResponse<?> getOptionSupplier();
    ApiResponse<?> getOptionColor();
    ApiResponse<?> getOptionSize();
    ApiResponse<?> getAllColor();
    ApiResponse<?> getAllSize();
    ApiResponse<?> createSize(SizeRequest sizeRequest);
    ApiResponse<?> createColor(ColorRequest colorRequest);
}
