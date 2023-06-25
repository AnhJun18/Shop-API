package com.myshop.api.service.order;

import com.myshop.api.payload.request.order.OrderRequest;
import com.myshop.api.payload.response.order.OrderResponse;
import com.myshop.repositories.order.entities.Order;
import org.springframework.data.domain.Page;

import java.util.Date;
import java.util.List;

public interface OrderService {
    OrderResponse order(Long userID, OrderRequest orderRequest) ;

    List<Order> getTheOrder(Long UserID);

    Iterable<Order> getAllOrderByAdmin();

    Iterable<Order> getTheOrderByStatus(Long UserID, String status);

    Iterable<Order> getOrderByStatusByAdmin(String status);

    OrderResponse confirmOrder(Long userID, Long idOrder);

    OrderResponse deliveryOrder( Long idOrder);

    OrderResponse confirmPaidOrder( Long idOrder);

    OrderResponse confirmCancelOrder( Long idOrder);

    OrderResponse cancelOrderByUser( Long idOrder);

    Page<Order> searchOrder(Date from, Date to, String query, String name, Integer page, Integer size) ;
}
