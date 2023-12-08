package com.myshop.repositories.stock.entites;

import com.myshop.repositories.product.entities.ProductDetail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Builder
@Entity
@IdClass(StockInDetailKey.class)
@Table(name = "STOCKIN_DETAIL")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockInDetail {

  @Id
  @ManyToOne
  @JoinColumn(name = "STOCKINID")
  private StockIn stockIn;

  @Id
  @ManyToOne
  @JoinColumn(name = "PRODUCTDETAILID")
  private ProductDetail productDetail;

  @Column(name = "QUANTITY")
  private Long quantity;

  @Column(name = "PRICESIN")
  private Long pricesIn;

}
