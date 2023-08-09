package com.myshop.repositories.order.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.myshop.repositories.product.entities.ProductDetail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Builder
@Entity
@IdClass(OrderDetailKey.class)
@Table(name = "ORDER_DETAIL")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetail {

    @Id
    @JoinColumn(name = "ORDERID")
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    private Order order;

    @Id
    @ManyToOne
    @JoinColumn(name = "PRODUCTDETAILID")
    private ProductDetail productDetail;

    @Column(name = "QUANTITYORDER")
    private Long quantityOrder;

    @Column(name = "SALEPRICE")
    private Long priceSale;

}
