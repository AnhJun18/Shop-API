package com.myshop.repositories.product.repos;

import com.myshop.repositories.product.entities.ShoppingCart;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShoppingCartRepository extends CrudRepository<ShoppingCart, Long> , JpaSpecificationExecutor<ShoppingCart> {

}
