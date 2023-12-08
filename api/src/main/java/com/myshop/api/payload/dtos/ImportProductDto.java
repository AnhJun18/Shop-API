package com.myshop.api.payload.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Data
public class ImportProductDto {
  private Long productId;
  private String productName;
  private Long colorId;
  private String colorName;
  private Long sizeId;
  private String sizeName;
  private Long quantity;
  private Long pricesIn;
}
