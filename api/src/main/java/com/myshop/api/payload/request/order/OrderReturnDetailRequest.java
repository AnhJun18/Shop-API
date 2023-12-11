package com.myshop.api.payload.request.order;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderReturnDetailRequest {
    private Long productDetailId;
    private Long quantityReturn;
}
