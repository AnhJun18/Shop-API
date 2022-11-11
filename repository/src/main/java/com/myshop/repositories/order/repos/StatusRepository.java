package com.myshop.repositories.order.repos;

import com.myshop.repositories.order.entities.Status;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StatusRepository extends CrudRepository<Status, Long> , JpaSpecificationExecutor<Status> {
    Optional<Status> findById(Long id);
}
