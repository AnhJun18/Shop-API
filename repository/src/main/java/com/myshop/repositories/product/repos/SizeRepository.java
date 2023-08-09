package com.myshop.repositories.product.repos;

import com.myshop.repositories.product.entities.Size;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SizeRepository extends CrudRepository<Size, Long>, JpaSpecificationExecutor<Size> {

}
