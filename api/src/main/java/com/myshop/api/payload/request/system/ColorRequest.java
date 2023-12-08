package com.myshop.api.payload.request.system;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ColorRequest {
  private String colorName;
  private String colorCode;
}
