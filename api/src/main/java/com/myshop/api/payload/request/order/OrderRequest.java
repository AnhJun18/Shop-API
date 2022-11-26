package com.myshop.api.payload.request.order;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class OrderRequest {
    private String nameReceiver;
    private String address;
    private String phoneReceiver;
    private Double feeShip;
    private String note;
    private List<OrderDetailRequest> listProduct;
}
