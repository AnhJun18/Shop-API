package com.myshop.repositories.order.repos;

import com.myshop.repositories.order.entities.OrderDetail;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderDetailRepository extends CrudRepository<OrderDetail, Long> , JpaSpecificationExecutor<OrderDetail> {

    List<OrderDetail> findAllByOrder_OrderId(Long orderId);

    OrderDetail findOrderDetailByOrder_OrderIdAndProductDetail_Id(Long orderId,Long productDetailId);
}
