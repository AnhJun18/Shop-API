package com.myshop.repositories.product_category.entities;

import com.myshop.repositories.category.entities.Category;
import com.myshop.repositories.product.entities.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Builder
@Entity
@IdClass(ProductCategoryKey.class)
@Table(name = "CATEGORY_PRODUCT")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductCategory {

    @Id
    @ManyToOne
    @JoinColumn(name = "PRODUCTID")
    private Product product;

    @Id
    @ManyToOne
    @JoinColumn(name = "CATEGORYCODE")
    private Category category;

}
