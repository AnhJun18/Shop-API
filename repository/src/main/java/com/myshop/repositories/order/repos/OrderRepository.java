package com.myshop.repositories.order.repos;

import com.myshop.repositories.order.entities.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Map;

@Repository
public interface OrderRepository extends CrudRepository<Order,Long>, JpaSpecificationExecutor<Order> {
   Iterable<Order> findAllByUserInfo_Id(Long id);

   Iterable<Order> findAllByStatus_Name(String name);

   Iterable<Order> findAllByUserInfo_IdAndStatus_Name(Long id,String statusName);

   Order findOrderById( Long id);

   @Query("SELECT u.id as id,u.phoneReceiver as phone, " +
           "u.feeShip as feeShip,u.userInfo.firstName as firstName , u.userInfo.lastName as lastName, sum(k.prices * k.amount)as summaryMoney " +
           "FROM Order  u join u.orderDetails k " +
           "where (?1 is NULL  OR u.createdDate >= ?1) " +
           "and (?2 is NULL  OR u.createdDate <= ?2)" +
           "and (?3 is NULL  OR u.status.name = ?3)" +
           "group by  u.id,u.address,u.feeShip,u.userInfo.firstName,u.userInfo.lastName,u.phoneReceiver"
           )
   Iterable<Map<String,Object>> findAllOrderToReport(Date from, Date to, String status);

   @Query("SELECT u.id as id,u.phoneReceiver as phone, " +
           "u.feeShip as feeShip,u.userInfo.firstName as firstName , u.userInfo.lastName as lastName, sum(k.prices)as summaryMoney " +
           "FROM Order  u join u.orderDetails k " +
           "where (?1 is NULL  OR u.createdDate >= ?1) " +
           "and (?2 is NULL  OR u.createdDate <= ?2)" +
           "and (?3 is NULL  OR u.status.name = ?3)" +
           "group by  u.id,u.address,u.feeShip,u.userInfo.firstName,u.userInfo.lastName,u.phoneReceiver,u.createdDate"
   )
   Page<Map<String,Object>> searchOrder(Date from, Date to, String status, Pageable pageable);

}
