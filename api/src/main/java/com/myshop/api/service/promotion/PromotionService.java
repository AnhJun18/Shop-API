package com.myshop.api.service.promotion;

import com.myshop.api.payload.request.promotion.AddPrToPromotionRequest;
import com.myshop.api.payload.request.promotion.PromotionRequest;
import com.myshop.repositories.promotion.entity.Promotion;

public interface PromotionService {

    Promotion createPromotion(PromotionRequest promotionRequest);
    Promotion addProductToPromotion(AddPrToPromotionRequest listProductID) throws Exception;

}
