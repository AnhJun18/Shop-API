package com.myshop.repositories.shopping_cart.repos;

import com.myshop.repositories.shopping_cart.entities.ShoppingCart;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ShoppingCartRepository extends CrudRepository<ShoppingCart, Long> , JpaSpecificationExecutor<ShoppingCart> {

    @Query("SELECT u.id, u.productDetail, u.amount FROM ShoppingCart u WHERE u.userInfo.id=:userId")
    Iterable<Object> findAllByUserInfo_Id(@Param("userId") Long id);


    ShoppingCart findShoppingCartByUserInfo_IdAndProductDetail_Id(Long userID, Long product);
}
