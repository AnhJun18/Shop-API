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

@Transactional
@Service
public class PromotionService {

  @Autowired
  PromotionRepository promotionRepository;

  public List<Promotion> getListPromotion() {
    return promotionRepository.findAll();
  }

  public ApiResponse<PromotionResponse> createPromotion(PromotionRequest promotionRequest) {
    Promotion promotion = Promotion.builder().description(promotionRequest.getDescription())
            .startDate(promotionRequest.getStartDate())
            .endDate(promotionRequest.getEndDate())
            .name(promotionRequest.getPromotionName())
            .description(promotionRequest.getDescription()).build();
    promotionRepository.save(promotion);
    for (ValuePromotionInPr valuePrApply : promotionRequest.getListApply()) {
      promotionRepository.createPromo(valuePrApply.getProductId(), promotion.getId(), valuePrApply.getValue());
    }
    return ApiResponse.of(PromotionResponse.builder().status(true).promotion(promotion).build());
  }

}
