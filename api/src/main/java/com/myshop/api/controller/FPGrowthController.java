package com.myshop.api.controller;


import com.myshop.api.payload.request.fpgrowth.FPRequest;
import com.myshop.api.service.fpgrowth.FPGrowthService;
import com.myshop.common.http.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * @author Anh Jun
 * @author Anh Jun
 */
@RestController
@RequestMapping("/api/fp-growth")
public class FPGrowthController {
  @Autowired
  FPGrowthService fpGrowthService;

  @GetMapping("/info")
  public Mono<ApiResponse<?>> initData(@RequestParam(defaultValue = "false") Boolean isTest) {
    return Mono.just(ApiResponse.builder().status(200).message("Success").data(fpGrowthService.infoInit(isTest)).build());
  }
  @PostMapping("/train")
  public Mono<ApiResponse<?>> trainFpGrowth(@RequestBody FPRequest fpRequest) {
      return Mono.just(ApiResponse.builder().data(fpGrowthService.trainFPGrowth(fpRequest)).status(200).message("Success").build());
  }

  @GetMapping("/get-options")
  public Mono<ApiResponse<?>> getOptsCategory(@RequestParam(defaultValue = "false") Boolean isTest) {
    return Mono.just(fpGrowthService.getOptionItem(isTest));
  }
}
