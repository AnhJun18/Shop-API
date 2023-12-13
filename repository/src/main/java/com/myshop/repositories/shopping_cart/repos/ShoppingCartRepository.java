package com.myshop.repositories.shopping_cart.repos;

import com.myshop.repositories.shopping_cart.entities.ShoppingCart;
import com.myshop.repositories.shopping_cart.entities.ShoppingCartKey;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShoppingCartRepository extends CrudRepository<ShoppingCart, ShoppingCartKey> , JpaSpecificationExecutor<ShoppingCart> {
    Optional<ShoppingCart> findShoppingCartByCustomerIdAndProductDetailId(Long customerId,Long productDetailId);
    List<ShoppingCart> findAllByCustomerId(Long customerId);
//    Optional<ShoppingCart> findShoppingCartByCustomerIdAndAndProductDetailId(Long customerId,Long productDetailId);


}
