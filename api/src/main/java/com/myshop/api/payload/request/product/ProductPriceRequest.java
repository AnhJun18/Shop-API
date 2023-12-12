package com.myshop.api.payload.request.product;

import lombok.Data;

import java.util.List;

@Data
public class ProductPriceRequest {
    private String fromDate;
    private List<ProductPriceDetailRequest> list_order;
}