package com.myshop.repositories.warehouse.repos;

import com.myshop.repositories.order.entities.WarehouseReceipt;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WarehouseReceiptRepository extends CrudRepository<WarehouseReceipt, Long> , JpaSpecificationExecutor< WarehouseReceipt> {

}
