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
    @Query(value = "exec SP_REPORT_ReportRevenue_ByDay :fromDate, :toDate", nativeQuery = true)
    Iterable<Map<String, Object>> reportRevenueByDay(@Param("fromDate") String fromDate,@Param("toDate")String toDate);
    @Query(value = "exec SP_REPORT_ReportRevenue_ByOrder :fromDate, :toDate", nativeQuery = true)
    Iterable<Map<String, Object>> reportRevenueByOrder(@Param("fromDate") String fromDate,@Param("toDate")String toDate);
    @Query(value = "exec SP_REPORT_PROFIT_BYDAY :fromDate, :toDate", nativeQuery = true)
    Iterable<Map<String, Object>> reportProfitByDay(@Param("fromDate") String fromDate,@Param("toDate")String toDate);
    @Query(value = "exec SP_REPORT_PROFIT_BYPRODUCT :fromDate, :toDate", nativeQuery = true)
    Iterable<Map<String, Object>> reportProfitByProduct(@Param("fromDate") String fromDate,@Param("toDate")String toDate);

    @Query(value = "exec SP_FP_TRANSACTION :minSup", nativeQuery = true)
    List<Map<String,Object>> getTransaction(@Param("minSup") Integer minSup);

  @Query(value = "exec SP_FP_TRANSACTION_TEST :minSup", nativeQuery = true)
  List<Map<String,Object>> getTransactionTest(@Param("minSup") Integer minSup);
  @Query(value = "exec SP_FP_OPTION_ITEMS ", nativeQuery = true)
  List<Map<String,Object>> getOptsItem();

  @Query(value = "exec SP_FP_OPTION_ITEMS_TEST ", nativeQuery = true)
  List<Map<String,Object>> getOptsItemTest();

    @Query(value = "exec SP_GET_INFO_DATA ", nativeQuery = true)
   Map<String,Object> getInfoInit();

  @Query(value = "exec SP_GET_INFO_DATA_TEST ", nativeQuery = true)
  Map<String,Object> getInfoInitTest();

    @Query(value = "exec SP_REPORT_Report_Inventory ", nativeQuery = true)
    Iterable<Map<String, Object>> reportInventory();
    List<Order> findAllByCustomerIdAndStatus_Id(Long customerId, Long statusId);

    Optional<Order> findOrderByCustomerIdAndOrderId(Long customerId, Long orderId);

    Optional<Order> findOrderByOrderId(Long orderId);

}
