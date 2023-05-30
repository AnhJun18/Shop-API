package com.myshop.api.controller;


import com.myshop.api.payload.request.promotion.AddPrToPromotionRequest;
import com.myshop.api.payload.request.promotion.PromotionRequest;
import com.myshop.api.service.promotion.PromotionService;
import com.myshop.common.http.ApiResponse;
import com.myshop.repositories.promotion.entity.Promotion;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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


    @GetMapping("/all")
    public List<Promotion> getAllPromotion() {
        return promotionService.getAllPromotion();
    }

    @GetMapping("/list-product/{id}")
    public List<Object> getListProductOfPromotion(@PathVariable Long id) {
        return promotionService.getListProductOfPromotion(id);
    }

    @PostMapping("/addProduct")
    public ApiResponse<?> addProductToPromotion(@RequestBody AddPrToPromotionRequest addPrToPromotionRequest) {

        try {
            promotionService.addProductToPromotion(addPrToPromotionRequest);
            return ApiResponse.builder().status(HttpStatus.SC_OK).message("Thêm khuyến mãi thành công").build();
        } catch (Exception e) {
            return ApiResponse.builder().status(HttpStatus.SC_INTERNAL_SERVER_ERROR).message(e.toString()).build();
        }
    }

    @DeleteMapping("/{id}")
    public ApiResponse<?> detelePromotion(@PathVariable Long id) {
        boolean result= promotionService.deletePromotion(id);
        return ApiResponse.builder().status(HttpStatus.SC_OK).message(result?"Xóa thành công":"Xóa thất bại").build();
    }


}
