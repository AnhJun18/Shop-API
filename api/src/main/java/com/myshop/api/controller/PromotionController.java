package com.myshop.api.controller;

import com.google.api.client.util.IOUtils;
import com.myshop.api.payload.request.promotion.PromotionRequest;
import com.myshop.api.payload.request.stockin.ImportRequest;
import com.myshop.api.payload.request.stockin.PreviewRequest;
import com.myshop.api.service.promotion.PromotionService;
import com.myshop.api.service.stockin.ImportService;
import com.myshop.common.http.ApiResponse;
import com.myshop.repositories.promotion.entity.Promotion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.AuditorAware;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("/api/promotion")
public class PromotionController {

  @Autowired
  PromotionService promotionService;

  @Autowired
  AuditorAware auditorProvider;

  @GetMapping("")
  public ApiResponse<List<Promotion>>  getListPromotion(){
    return ApiResponse.of(promotionService.getListPromotion()) ;
  }
  @PostMapping("")
  public ApiResponse<?>  createPromotion(@RequestBody PromotionRequest promotionRequest){
    return promotionService.createPromotion(promotionRequest);
  }
}
