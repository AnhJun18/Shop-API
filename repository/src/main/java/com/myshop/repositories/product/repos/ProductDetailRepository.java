package com.myshop.repositories.product.repos;

import com.myshop.repositories.product.entities.ProductDetail;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductDetailRepository extends CrudRepository<ProductDetail, Long>, JpaSpecificationExecutor<ProductDetail> {
  Optional<ProductDetail> findById(Long id);

  Optional<ProductDetail> findByProductAndColorAndSize(Long productId, Long colorId, Long sizeId);
}
