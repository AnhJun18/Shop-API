package com.myshop.repositories.warehouse.repos;

import com.myshop.repositories.warehouse.entities.WarehouseReceipt;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Map;

@Repository
public interface WarehouseReceiptRepository extends CrudRepository<WarehouseReceipt, Long>, JpaSpecificationExecutor<WarehouseReceipt> {
    @Query("SELECT u.id as id, u.createdDate as createdDate, sum(k.amount) as totalAmount, " +
            " sum(k.amount*k.costPrices) as totalMoney " +
            "FROM WarehouseReceipt u left join  WarehouseReceiptDetail k " +
            "on u.id=k.warehouseReceipt.id" +
            " group by u.id,u.createdDate")
    Iterable<Map<String, Object>> getAllReceipt(Instant from, Instant to);
}
