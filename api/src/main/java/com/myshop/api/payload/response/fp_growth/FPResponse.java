package com.myshop.api.payload.response.fp_growth;

import com.myshop.repositories.product.entities.ProductDetail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
public class FPResponse {
  private List<String> mapTrans;
  private List<String> data;
  private Long totalTime;
}
