package com.myshop.api.service.order;

import com.myshop.api.payload.request.order.OrderRequest;
import com.myshop.api.payload.request.order.OrderReturnRequest;
import com.myshop.api.payload.response.order.OrderResponse;
import com.myshop.common.http.ApiResponse;

public interface OrderService {
    OrderResponse order(String userOrder,OrderRequest orderRequest) ;

    ApiResponse<?> getStateOrder(Long orderId) ;

    ApiResponse<?> getHistoryOrderByStatus(String user,String statusName) ;

    ApiResponse<?> getListOrder(String search,Integer statusId,String fromDate,String toDate, Integer page, Integer itemsPerPage);

    ApiResponse<?> getOptionStatus();

    ApiResponse<?> getDetailById(Long orderId);

    ApiResponse<?> confirmOrder(Long orderId, String employeeReview);

    ApiResponse<?> addDeliveryOrder(Long orderId, String employeeDelivery);

    ApiResponse<?> confirmDeliveryOrder(Long orderId);

    ApiResponse<?> cancelOrderById(String user,Long orderId);

    ApiResponse<?> returnOrder(String userOrder, OrderReturnRequest orderRequest) throws Exception;


}