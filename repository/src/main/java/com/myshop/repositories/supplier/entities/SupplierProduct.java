package com.myshop.repositories.supplier.entities;

import com.myshop.repositories.product.entities.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Builder
@Entity
@IdClass(SupplierProductKey.class)
@Table(name = "SUPPLIER_PRODUCT")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupplierProduct {

  @Id
  @ManyToOne
  @JoinColumn(name = "PRODUCTID")
  private Product product;

  @Id
  @ManyToOne
  @JoinColumn(name = "SUPPLIERID")
  private Supplier supplier;

}
