package com.myshop.repositories.promotion.repos;

import com.myshop.repositories.promotion.entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long> {


    @Query(value = "select b.id as id,b.name as name, " +
            "   b.link_img as img, b.price as price, " +
            "   b.describe as describe " +
            "from product_promotions  a " +
            "join product b on a.product_id=b.id " +
            "where promotions_id = :idKM and b.delete_flag= 0",nativeQuery = true)
    List<Object> getProductApplyPromotion(@Param("idKM") Long idKM);
}
