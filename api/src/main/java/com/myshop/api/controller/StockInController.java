package com.myshop.api.controller;

import com.google.api.client.util.IOUtils;
import com.myshop.api.payload.request.stockin.ImportRequest;
import com.myshop.api.payload.request.stockin.PreviewRequest;
import com.myshop.api.service.stockin.ImportService;
import com.myshop.common.http.ApiResponse;
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

@RestController
@RequestMapping("/api/import")
public class StockInController {

  @Autowired
  ImportService importService;

  @Autowired
  AuditorAware auditorProvider;

  @GetMapping("/download-template")
  public ApiResponse<String> getTemplate() {
    String base64Excel = null;
    try {
      String templatePath = "templates/TemplateImport.xlsx";
      ClassPathResource resource = new ClassPathResource(templatePath);
      InputStream inputStream = resource.getInputStream();
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      IOUtils.copy(inputStream, outputStream);
      byte[] excelBytes = outputStream.toByteArray();
      base64Excel = Base64.getEncoder().encodeToString(excelBytes);
    } catch (Exception ex) {
      return ApiResponse.<String>builder().status(500).data(null).message("Lỗi đọc template").build();
    }
    return ApiResponse.<String>builder().status(200).data(base64Excel).build();
  }

  @PostMapping("/preview-excel")
  public ApiResponse<?> previewFromExcel(@RequestBody PreviewRequest previewRequest){
    try {
      return ApiResponse.of(importService.previewFromExcel(previewRequest));
    } catch (Exception ex) {
      String msgError = "Lỗi đọc excel: " + ex.getMessage();
      return ApiResponse.builder().message(msgError).status(500).data(null).build();
    }
  }


  @PostMapping("/import-excel")
  public ApiResponse<?> importFromExcel(@RequestBody ImportRequest importRequest) {
    try {
      importService.importFromExcel(auditorProvider.getCurrentAuditor().get().toString(), importRequest);
      return ApiResponse.builder().message("Nhập hàng thành công").status(200).data(true).build();
    } catch (Exception ex) {
      String msgError = "Lỗi nhập hàng: " + ex.getMessage();
      return ApiResponse.builder().message(msgError).status(500).data(null).build();
    }
  }

}
