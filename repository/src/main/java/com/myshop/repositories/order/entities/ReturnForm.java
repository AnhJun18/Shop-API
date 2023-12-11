package com.myshop.repositories.order.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.myshop.repositories.user.entities.Employee;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Builder
@Entity
@Table(name = "RETURN_FORM")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReturnForm {

    @Id
    @Column(name = "RETURNFORMID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long returnFormId;

    @OneToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    @JoinColumn(name = "ORDERID",nullable = false)
    private Order orderId;

    @OneToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    @JoinColumn(name = "EMPLOYEECREATE",nullable = false)
    private Employee employee;
}