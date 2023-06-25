package com.myshop.api.service.promotion;

import com.myshop.api.payload.request.promotion.AddPrToPromotionRequest;
import com.myshop.api.payload.request.promotion.PromotionRequest;
import com.myshop.repositories.promotion.entity.Promotion;

import java.util.List;

public interface PromotionService {

    Promotion createPromotion(PromotionRequest promotionRequest);

    List<Promotion> getAllPromotion();

    boolean deletePromotion(Long id);

    public List<Object> getListProductOfPromotion(Long id);

    Promotion addProductToPromotion(AddPrToPromotionRequest listProductID) throws Exception;

    Promotion removeProductToPromotion(AddPrToPromotionRequest listProductID) throws Exception;

}
