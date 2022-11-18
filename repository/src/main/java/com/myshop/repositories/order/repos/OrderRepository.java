package com.myshop.repositories.order.repos;

import com.myshop.repositories.order.entities.Order;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public interface OrderRepository extends CrudRepository<Order,Long>, JpaSpecificationExecutor<Order> {
   Iterable<Order> findAllByUserInfo_Id(Long id);

   Iterable<Order> findAllByStatus_Name(String name);

   Iterable<Order> findAllByUserInfo_IdAndStatus_Name(Long id,String statusName);

   Order findOrderById( Long id);

   @Query("SELECT u.id as id,u.phoneReceiver as phone, " +
           "u.feeShip as feeShip,u.userInfo.firstName as firstName , u.userInfo.lastName as lastName, sum(k.prices)as summaryMoney " +
           "FROM Order  u join u.orderDetails k " +
           "group by  u.id,u.address,u.feeShip,u.userInfo.firstName,u.userInfo.lastName,u.phoneReceiver"
           )
   Iterable<Map<String,Object>> findAllOrderToReport();

}
