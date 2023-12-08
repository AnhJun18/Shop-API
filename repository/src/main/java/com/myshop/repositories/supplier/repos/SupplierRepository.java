package com.myshop.repositories.supplier.repos;

import com.myshop.repositories.supplier.entities.Supplier;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface SupplierRepository extends CrudRepository<Supplier, Long> , JpaSpecificationExecutor<Supplier> {

}
