package com.myshop.repositories.order.repos;

import com.myshop.repositories.order.entities.Order;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface OrderRepository extends CrudRepository<Order, Long>, JpaSpecificationExecutor<Order> {
    @Query(value = "exec SP_ORDER_reportRevenue_ByDay :fromDate, :toDate", nativeQuery = true)
    Iterable<Map<String, Object>> reportRevenueByDay(@Param("fromDate") String fromDate,@Param("toDate")String toDate);


    @Query(value = "exec SP_REPORT_ReportRevenue_ByOrder :fromDate, :toDate", nativeQuery = true)
    Iterable<Map<String, Object>> reportRevenueByOrder(@Param("fromDate") String fromDate,@Param("toDate")String toDate);

    List<Order> findAllByCustomerIdAndStatus_Id(Long customerId, Long statusId);

    Optional<Order> findOrderByCustomerIdAndOrderId(Long customerId, Long orderId);

}
