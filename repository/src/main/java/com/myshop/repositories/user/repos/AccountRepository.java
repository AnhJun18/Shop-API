package com.myshop.repositories.user.repos;
import com.myshop.repositories.user.entities.Account;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends CrudRepository<Account, Long>, JpaSpecificationExecutor<Account> {

    @Query("SELECT u FROM Account u WHERE u.username=:userName")
    Account findAccountByUsername(@Param("userName") String userName);

    Iterable<Account> findAllByDeleteFlag(boolean deleteFlag);

    Account findAccountByEmail(String email);

    boolean existsByEmail( String email);
}
