package com.myshop.api.service.order;

import com.myshop.api.payload.request.order.OrderRequest;
import com.myshop.api.payload.response.order.OrderResponse;
import com.myshop.repositories.order.entities.Order;

import java.util.List;

public interface OrderService {
    OrderResponse order(Long userID,OrderRequest orderRequest);

    List<Order> getTheOrder(Long UserID);

    Iterable<Order> getAllOrderByAdmin();

    List<Order> getTheOrderByStatus(Long UserID, String status);

    Iterable<Order> getOrderByStatusByAdmin( String status);

    OrderResponse confirmOrder(Long userID,Long idOrder, String status);
}
