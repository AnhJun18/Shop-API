package com.myshop.repositories.shopping_cart.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShoppingCartKey implements Serializable {

    private Long productDetailId;
    private Long customerId;


}
