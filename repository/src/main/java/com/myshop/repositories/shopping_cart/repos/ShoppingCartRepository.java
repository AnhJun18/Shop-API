package com.myshop.repositories.shopping_cart.repos;

import com.myshop.repositories.shopping_cart.entities.ShoppingCart;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShoppingCartRepository extends CrudRepository<ShoppingCart, Long> , JpaSpecificationExecutor<ShoppingCart> {

}
