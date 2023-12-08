package com.myshop.api.controller;


import com.google.api.client.util.IOUtils;
import com.myshop.api.payload.request.stockin.RequestSupplierRequest;
import com.myshop.api.payload.request.system.ColorRequest;
import com.myshop.api.payload.request.system.SizeRequest;
import com.myshop.api.service.supplier.SupplierService;
import com.myshop.common.http.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

/**
 * @author Anh Jun
 * @author Anh Jun
 */

@Slf4j
@RestController
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/api/supplier")
public class SupplierController {

  @Autowired
  SupplierService supplierService;

  @PostMapping("/request/export-excel")
  public ApiResponse<String> exportFromExcel(@RequestBody RequestSupplierRequest exportRequest) throws IOException {
    String base64Excel = null;
    try {
      ByteArrayOutputStream outputStream= supplierService.exportExcelRequestSupplier(exportRequest);
      byte[] excelBytes = outputStream.toByteArray();
      base64Excel = Base64.getEncoder().encodeToString(excelBytes);
    } catch (Exception ex) {
      return ApiResponse.<String>builder().status(500).data(null).message("Lỗi tạo file").build();
    }
    return ApiResponse.<String>builder().status(200).data(base64Excel).build();
  }

  @GetMapping("/get-options")
  public Mono<ApiResponse<?>> getOptsSupplier() {
    return Mono.just(supplierService.getOptionSupplier());
  }

  @GetMapping("/color/get-options")
  public Mono<ApiResponse<?>> getOptionColor() {
    return Mono.just(supplierService.getOptionColor());
  }

  @GetMapping("/size/get-options")
  public Mono<ApiResponse<?>> getOptionSize() {
    return Mono.just(supplierService.getOptionSize());
  }

  @GetMapping("/color")
  public Mono<ApiResponse<?>> getAllColor() {
    return Mono.just(supplierService.getAllColor());
  }

  @PostMapping("/color")
  public Mono<ApiResponse<?>> createColor(@RequestBody ColorRequest colorRequest) {
    return Mono.just(supplierService.createColor(colorRequest));
  }

  @GetMapping("/size")
  public Mono<ApiResponse<?>> getAllSize() {
    return Mono.just(supplierService.getAllSize());
  }

  @PostMapping("/size")
  public Mono<ApiResponse<?>> createSize(@RequestBody  SizeRequest sizeRequest) {
    return Mono.just(supplierService.createSize(sizeRequest));
  }
}