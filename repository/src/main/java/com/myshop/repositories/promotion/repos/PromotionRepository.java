package com.myshop.repositories.promotion.repos;

import com.myshop.repositories.promotion.entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long> {

}
