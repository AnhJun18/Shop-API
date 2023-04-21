package com.myshop.repositories.shipment.repos;

import com.myshop.repositories.shipment.entities.Shipment;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShipmentRepository extends CrudRepository<Shipment, Long>, JpaSpecificationExecutor<Shipment> {

}
