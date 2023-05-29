package com.myshop.api.controller;


import com.myshop.api.payload.request.promotion.AddPrToPromotionRequest;
import com.myshop.api.payload.request.promotion.PromotionRequest;
import com.myshop.api.service.promotion.PromotionService;
import com.myshop.common.http.ApiResponse;
import com.myshop.repositories.promotion.entity.Promotion;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("api/admin/promotion")
public class PromotionController {
    @Autowired
    PromotionService promotionService;

    @PostMapping()
    public Promotion createPromotion(@RequestBody PromotionRequest promotionRequest) {
        return promotionService.createPromotion(promotionRequest);
    }

    @PostMapping("/addProduct")
    public ApiResponse<?> addProductToPromotion(@RequestBody AddPrToPromotionRequest addPrToPromotionRequest) {

        try {
            Promotion promotion = promotionService.addProductToPromotion(addPrToPromotionRequest);
            return ApiResponse.builder().status(HttpStatus.SC_OK).message("Thêm khuyến mãi thành công").build();
        } catch (Exception e) {
            return ApiResponse.builder().status(HttpStatus.SC_INTERNAL_SERVER_ERROR).message(e.toString()).build();
        }
    }
}
