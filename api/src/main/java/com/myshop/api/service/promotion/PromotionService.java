package com.myshop.api.service.promotion;

import com.myshop.api.payload.request.promotion.PromotionRequest;
import com.myshop.api.payload.request.promotion.ValuePromotionInPr;
import com.myshop.api.payload.response.promotion.PromotionResponse;
import com.myshop.common.http.ApiResponse;
import com.myshop.repositories.promotion.entity.Promotion;
import com.myshop.repositories.promotion.repos.PromotionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
@Service
public class PromotionService {

  @Autowired
  PromotionRepository promotionRepository;

  public List<Promotion> getListPromotion() {
    return promotionRepository.findAllByIsStoped(false);
  }

  @Transactional
  public ApiResponse<PromotionResponse> createPromotion(PromotionRequest promotionRequest) {
    Promotion promotion = Promotion.builder().description(promotionRequest.getDescription())
            .startDate(promotionRequest.getStartDate())
            .endDate(promotionRequest.getEndDate())
            .name(promotionRequest.getPromotionName())
            .isStoped(false)
            .description(promotionRequest.getDescription()).build();
    promotionRepository.save(promotion);
    for (ValuePromotionInPr valuePrApply : promotionRequest.getListApply()) {
      promotionRepository.createPromo(valuePrApply.getProductId(), promotion.getId(), valuePrApply.getPercentApply());
    }
    return ApiResponse.of(PromotionResponse.builder().status(true).promotion(promotion).build());
  }

  @Transactional
  public ApiResponse<?> stopPromotion(Long promotionId) {
    Optional<Promotion> promotion = promotionRepository.findPromotionById(promotionId);
    if(promotion.isPresent()){
      promotion.get().setIsStoped(true);
      promotionRepository.save(promotion.get());
      return ApiResponse.of(PromotionResponse.builder().status(true).message("Khuyến mãi đã được khóa").build());
    }else
      return ApiResponse.of(PromotionResponse.builder().status(false).message("Không tìm thấy thông tin khuyến mãi").build());
  }
}
