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
    private String phoneReceiver;
    private String addressReceiver;
    private String note;
    private List<OrderDetailRequest> orderItems;
}
