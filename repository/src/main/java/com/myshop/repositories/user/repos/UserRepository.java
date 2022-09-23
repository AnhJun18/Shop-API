package com.myshop.repositories.user.repos;
import com.myshop.repositories.user.entities.Account;
import com.myshop.repositories.user.entities.User;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Long>, JpaSpecificationExecutor<User> {

    @Query("SELECT u FROM User u WHERE u.account=:account")
    User findUserByAccount(@Param("account") Account account);

    boolean existsByPhone( String phone);
}
