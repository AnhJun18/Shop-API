package com.myshop.repositories.user.repos;
import com.myshop.repositories.user.entities.Account;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public interface AccountRepository extends CrudRepository<Account, String>, JpaSpecificationExecutor<Account> {

    Account findAccountByEmail(String email);

    @Query(value = "EXEC USER_GetByEmail :email",nativeQuery = true)
    Map<String,Object> getProfileByEmail(@Param("email") String email);

//    Iterable<Account> findAllByDeleteFlag(boolean deleteFlag);
//
//    Account findAccountByEmail(String email);
//
    boolean existsByEmail( String email);
}
