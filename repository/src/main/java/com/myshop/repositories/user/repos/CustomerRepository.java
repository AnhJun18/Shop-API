package com.myshop.repositories.user.repos;

import com.myshop.repositories.user.entities.Customer;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends CrudRepository<Customer, Long>, JpaSpecificationExecutor<Customer> {
//
//    @Query("SELECT u FROM UserInfo u WHERE u.account=:account")
//    Customer findUserByAccount(@Param("account") Account account);
//
//    Iterable<Customer> findAllByAccount_DeleteFlagAndAccount_Role_Name(boolean deleteFlag,String roleName);
//
    Optional<Customer> findByEmail(String email);
//
    boolean existsByPhone(String phone);
}
