package com.myshop.api.payload.request.promotion;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ValuePromotionInPr {
  private Long productId;
  @Min(1)
  @Max(100)
  private Integer value;
}
