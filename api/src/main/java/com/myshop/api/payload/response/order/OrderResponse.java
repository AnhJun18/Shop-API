package com.myshop.api.payload.response.order;

import com.myshop.repositories.order.entities.Order;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class OrderResponse {
    private Integer status;
    private String message;
    private Order data;
}
