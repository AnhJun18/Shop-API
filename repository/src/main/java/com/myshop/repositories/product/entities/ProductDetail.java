package com.myshop.repositories.product.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Builder
@Entity
@Table(name = "PRODUCT_DETAIL")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PRODUCTDETAILID")
    private Long id;

    @Column(name="PRODUCTID")
    private Long product;

    @Column(name = "SIZEID")
    private Long size;

    @Column(name = "COLORID")
    private Long color;

    @Column(name = "QUANTITY")
    private Long quantity;

}
