package com.myshop.repositories.order.repos;

import com.myshop.repositories.order.entities.Order;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends CrudRepository<Order, Long> , JpaSpecificationExecutor<Order> {
   Iterable<Order> findAllByUserInfo_Id(Long id);

   Iterable<Order> findAllByUserInfo_IdAndStatus_Name(Long id,String statusName);

   Order findOrderById( Long id);
}
