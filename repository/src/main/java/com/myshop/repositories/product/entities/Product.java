package com.myshop.repositories.product.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Builder
@Entity
@Table(name = "PRODUCT")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PRODUCTID")
    private Long id;

    @Column(name = "PRODUCTNAME")
    private String name;

    @Column(name = "DEFAULTIMAGE")
    private String linkImg;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "VIEWED")
    private Long viewed;

    @Column(name = "ISDELETED")
    private boolean isDeleted;

}
