package com.myshop.repositories.order.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Builder
@Entity
@Table(name = "BILL")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Bill {

    @Id
    @Column(name = "BILLID")
    private String billId;

    @OneToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    @JoinColumn(name = "ORDERID",nullable = false)
    private Order orderId;

}