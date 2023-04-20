package com.myshop.repositories.payment.repos;

import com.myshop.repositories.payment.entities.Payment;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends CrudRepository<Payment, Long>, JpaSpecificationExecutor<Payment> {

}
