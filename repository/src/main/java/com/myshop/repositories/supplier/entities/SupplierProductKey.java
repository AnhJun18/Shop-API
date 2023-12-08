package com.myshop.repositories.supplier.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupplierProductKey implements Serializable {
  private Long supplier;
  private Long product;
}
