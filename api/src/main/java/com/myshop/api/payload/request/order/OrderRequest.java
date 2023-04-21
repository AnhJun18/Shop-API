package com.myshop.api.payload.request.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class OrderRequest {
    private String nameReceiver;
    private String province;
    private String district;
    private String ward;
    private String address;
    private String phoneReceiver;
    private String note;
    private List<OrderDetailRequest> listProduct;
}
