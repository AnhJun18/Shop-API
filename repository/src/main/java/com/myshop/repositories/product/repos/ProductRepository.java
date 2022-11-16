package com.myshop.repositories.product.repos;

import com.myshop.repositories.product.entities.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends CrudRepository<Product, Long> , JpaSpecificationExecutor<Product> {

    Page<Product> findAll( Pageable pageable);

    Iterable<Product> findAllByCategory_Name(String nameCategory);
}
