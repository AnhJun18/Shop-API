package com.myshop.repositories.product.repos;

import com.myshop.repositories.product.entities.Product;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ProductRepository extends CrudRepository<Product, Long>, JpaSpecificationExecutor<Product> {
  @Modifying
  @Transactional
  @Query(value = "INSERT INTO [dbo].[PRICEVOLATILITY]([PRODUCTID],[EMPLOYEEID],[TIMECHANGE],[PRICE]) " +
          "     VALUES(:productId,:employeeId,:timeChange,:newPrice)", nativeQuery = true)
  void updatePrice(@Param("productId") Long productId, @Param("employeeId") Long employeeId, @Param("timeChange") String timeChange,@Param("newPrice") Long newPrice);
}
