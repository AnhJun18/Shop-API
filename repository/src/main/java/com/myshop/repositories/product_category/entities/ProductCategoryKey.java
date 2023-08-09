package com.myshop.repositories.product_category.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductCategoryKey implements Serializable {

    private Long product;
    private String category;

    // Phương thức equals và hashCode

}
