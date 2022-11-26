package com.myshop.repositories.order.repos;

import com.myshop.repositories.order.entities.OrderDetail;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderDetailRepository extends CrudRepository<OrderDetail, Long> , JpaSpecificationExecutor<OrderDetail> {

}
