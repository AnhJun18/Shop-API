
package com.myshop.repositories.warehouse.repos;

import com.myshop.repositories.warehouse.entity.WarehouseReceiptDetail;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WarehouseReceiptDetailRepository extends CrudRepository<WarehouseReceiptDetail, Long>, JpaSpecificationExecutor<WarehouseReceiptDetail> {

}
