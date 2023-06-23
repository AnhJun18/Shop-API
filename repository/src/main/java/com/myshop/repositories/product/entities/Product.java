package com.myshop.repositories.product.entities;

import com.myshop.repositories.promotion.entity.Promotion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Builder
@Entity
@Table(name = "product")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "nvarchar(255)",unique = true)
    private String name;

    private String linkImg;

    @ManyToOne(fetch = FetchType.EAGER)
    private Category category;

    @Column(columnDefinition = "nvarchar(255)")
    private String describe;

    @OneToMany(fetch = FetchType.EAGER)
    private List<Promotion> promotions;

    private Integer price;

    private Long sold;

    private boolean deleteFlag;

    public Product(String name, String linkImg, Category category, String describe, Integer price, Long sold, boolean deleteFlag) {
        this.name = name;
        this.linkImg = linkImg;
        this.category = category;
        this.describe = describe;
        this.price = price;
        this.sold = sold;
        this.deleteFlag = deleteFlag;
    }
}
