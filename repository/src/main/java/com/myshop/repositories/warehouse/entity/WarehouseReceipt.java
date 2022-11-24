package com.myshop.repositories.order.entities;

import com.myshop.repositories.Auditing;
import com.myshop.repositories.product.entities.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Builder
@Entity
@Table(name = "warehouse_receipt")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseReceipt extends Auditing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne()
    private Product product;

    private String color;

    private String size;

    private Long amount;

    private Double costPrice;



}