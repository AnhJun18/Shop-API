package com.myshop.repositories.stock.entites;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockInDetailKey implements Serializable {
  private Long stockIn;
  private Long productDetail;
}
