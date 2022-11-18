package com.myshop.api.service.order;

import com.myshop.api.payload.request.order.OrderRequest;
import com.myshop.api.payload.response.order.OrderResponse;
import com.myshop.repositories.order.entities.Order;
import org.springframework.data.domain.Page;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface OrderService {
    OrderResponse order(Long userID, OrderRequest orderRequest);

    List<Order> getTheOrder(Long UserID);

    Iterable<Order> getAllOrderByAdmin();

    List<Order> getTheOrderByStatus(Long UserID, String status);

    Iterable<Order> getOrderByStatusByAdmin(String status);

    OrderResponse confirmOrder(Long userID, Long idOrder);

    Page<Map<String, Object>> searchOrder(Date from, Date to, String name, Integer page, Integer size) ;
}
