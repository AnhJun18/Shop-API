package com.myshop.repositories.shopping_cart.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Builder
@Entity
@IdClass(ShoppingCartKey.class)
@Table(name = "SHOPPING_CART")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShoppingCart {
    @Id
    @Column(name = "PRODUCTDETAILID")
    private Long productDetailId;

    @Id
    @Column(name = "CUSTOMERID")
    private Long customerId;

    @Column(name = "QUANTITY")
    private Long quantity;


}
