package com.myshop.repositories.order.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailKey implements Serializable {

    private Long productDetail;
    private Long order;

    // Phương thức equals và hashCode

}
