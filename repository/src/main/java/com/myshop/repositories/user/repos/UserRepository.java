package com.myshop.repositories.user.repos;
import com.myshop.repositories.user.entities.Account;
import com.myshop.repositories.user.entities.UserInfo;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<UserInfo, Long>, JpaSpecificationExecutor<UserInfo> {

    @Query("SELECT u FROM UserInfo u WHERE u.account=:account")
    UserInfo findUserByAccount(@Param("account") Account account);

    boolean existsByPhone( String phone);
}
