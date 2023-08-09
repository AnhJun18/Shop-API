package com.myshop.repositories.order.repos;

import com.myshop.repositories.order.entities.Bill;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BillRepository extends CrudRepository<Bill, String>, JpaSpecificationExecutor<Bill> {

}
