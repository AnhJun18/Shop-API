package com.myshop.repositories.shipment.repos;

import com.myshop.repositories.shipment.entities.GHTKStatus;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository

public interface GHTKStatusRepository  extends CrudRepository<GHTKStatus, Long>, JpaSpecificationExecutor<GHTKStatus> {

    Optional<GHTKStatus> findByCode(Integer code);
}
