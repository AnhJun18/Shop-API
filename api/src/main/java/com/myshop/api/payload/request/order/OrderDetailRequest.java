package com.myshop.api.payload.request.order;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderDetailRequest {
    private Long productDetailId;
    private Long quantity;
    private Long price;
}
