package com.myshop.repositories.order.repos;

import com.myshop.repositories.order.entities.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public interface OrderRepository extends CrudRepository<Order, Long>, JpaSpecificationExecutor<Order> {
    Iterable<Order> findAllByUserInfo_Id(Long id);

    Iterable<Order> findAllByStatus_Name(String name);

    Iterable<Order> findAllByUserInfo_IdAndStatus_Name(Long id, String statusName);

    Order findOrderById(Long id);

    @Query("SELECT u.id as id,u.shipment.phoneReceiver as phone, " +
            "u.feeShip as feeShip,u.userInfo.firstName as firstName , u.userInfo.lastName as lastName, sum(k.prices * k.amount)as summaryMoney " +
            "FROM Order  u join u.orderDetails k " +
            "where (?1 is NULL  OR u.createdDate >= ?1) " +
            "and (?2 is NULL  OR u.createdDate <= ?2)" +
            "and (?3 is NULL  OR u.status.name = ?3)" +
            "group by  u.id,u.shipment.address,u.feeShip,u.userInfo.firstName,u.userInfo.lastName,u.shipment.phoneReceiver"
    )
    Iterable<Map<String, Object>> findAllOrderToReport(Date from, Date to, String status);

    @Query(" SELECT  u as orderInfo FROM Order  u " +
            "where (:from is NULL  OR :to is NULL  OR u.createdDate BETWEEN :from AND :to)" +
            "and (:status is NULL  OR u.status.name like %:status%)" +
            "and (u.createdDate is not null ) " +
            "and (:info is null OR u.shipment.phoneReceiver like %:info% OR u.shipment.nameReceiver like %:info% )"
    )
    Page<Order> searchOrder(@Param("from") Instant from, @Param("to") Instant to, @Param("info") String info, @Param("status") String status, Pageable pageable);

    @Query(value = "SELECT Day(u.created_date) as ngay, sum(od.amount) as total, " +
            "sum(od.amount*od.prices) as totalMoney " +
            "FROM the_order  u left join order_detail od " +
            "on u.id = od.order_id " +
            " where u.status_id = 4 " +
            "and MONTH(u.created_date)=:month " +
            "and Year(u.created_date)=:year " +
            "group by Day(u.created_date)", nativeQuery = true
    )
    Iterable<Map<String, Object>> reportMonthlyRevenue(@Param("month") int month, @Param("year") int year);

    @Query(value = "select p.id  as id, p.name  as name," +
            "sum(od.amount) as quantity," +
            "sum(od.prices * od.amount) as totalMoney " +
            "from product p " +
            "left join product_detail pd " +
            "   on p.id = pd.product " +
            "   left join order_detail od " +
            "       on pd.id = od.product_id " +
            "       left join the_order o " +
            "           on od.order_id = o.id " +
            "where o.created_date is null " +
            "      or o.created_date between :from and  :to " +
            "group by p.id, p.name", nativeQuery = true
    )
    Iterable<Map<String, Object>> reportProductRevenue(@Param("from") Instant from, @Param("to") Instant to);

    @Query(value = "  SELECT Month(o.created_date) as Thang," +
            " sum(od.prices*od.amount+o.fee_ship) as DoanhThu " +
            "FROM the_order  o  join payment p " +
            "on o.id=p.order_id " +
            "join order_detail od " +
            "on o.id= od.order_id " +
            "where Year(o.created_date) =:year " +
            "group by Month(o.created_date) ", nativeQuery = true
    )
    List<Map<String,Object>> getRevenueInYear(@Param("year") String year);
}
