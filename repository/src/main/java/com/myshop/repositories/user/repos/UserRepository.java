package com.myshop.repositories.user.repos;
import com.myshop.repositories.user.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Long>, JpaSpecificationExecutor<User> {

    @Query("SELECT u FROM User u WHERE u.username=:userName")
    User findUserByUsername(@Param("userName") String userName);

    Iterable<User> findAllByDeleteFlag(int deleteFlag);

    Page<User> findAllByDeleteFlag(int deleteFlag, Pageable pageable);

}
