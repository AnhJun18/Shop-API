package com.myshop.api.payload.request.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class OrderReturnRequest {
    private Long orderReturn;
    private List<OrderReturnDetailRequest> returnItems;
}
