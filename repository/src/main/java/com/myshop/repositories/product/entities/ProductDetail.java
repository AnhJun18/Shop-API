package com.myshop.repositories.product.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Builder
@Entity
@Table(name = "product_detail",
        uniqueConstraints =@UniqueConstraint(columnNames = {"product", "size","color"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.EAGER)
//    @JsonBackReference
    @JoinColumn(name = "product")
    private Product infoProduct;

    @Column(nullable = false)
    private String size;

    @Column(nullable = false)
    private String color;

    private Long current_number;

}
