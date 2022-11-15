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
@Table(name = "order_detail", uniqueConstraints =@UniqueConstraint(columnNames = {"order_id", "product_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    @JsonBackReference
    private Order order;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private ProductDetail productDetail;

    private Long amount;

    private Double prices;

}
