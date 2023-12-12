package com.myshop.repositories.promotion.repos;

import com.myshop.repositories.promotion.entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long> {

    List<Promotion> findAllByIsStoped(Boolean isStopped);

    Optional<Promotion> findPromotionById(Long promotionID);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO [dbo].[PRODUCT_PROMOTION] (PRODUCTID, PROMOTIONID, VALUE) " +
            "VALUES (:productId, :promotionId, :valuePromo)", nativeQuery = true)
    void createPromo(@Param("productId") Long productId, @Param("promotionId") Long promotionId, @Param("valuePromo") Integer valuePromo);
}
