package com.myshop.repositories.supplier.repos;

import com.myshop.repositories.supplier.entities.SupplierProduct;
import com.myshop.repositories.supplier.entities.SupplierProductKey;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface SupplierProductRepository extends CrudRepository<SupplierProduct, SupplierProductKey> , JpaSpecificationExecutor<SupplierProduct> {
}
