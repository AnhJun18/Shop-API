package com.myshop.api.payload.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Data
public class RequestSupplierDto {
  private Long productId;
  private String productName;
  private String color;
  private String size;
  private Long quantity;
  private Long price;
}
