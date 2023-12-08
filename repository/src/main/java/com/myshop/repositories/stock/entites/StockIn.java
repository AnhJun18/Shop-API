package com.myshop.repositories.stock.entites;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Builder
@Entity
@Table(name = "STOCKIN")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockIn {

    @Id
    @Column(name = "STOCKINID")
    private Long stockInId;

    @Column(name = "SUPPLIERID")
    private String supplierId;

    @Column(name = "EMPLOYEECREATED")
    private Long employeeCreated;
}
