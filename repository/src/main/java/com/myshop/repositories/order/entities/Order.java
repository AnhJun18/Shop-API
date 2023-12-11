package com.myshop.repositories.order.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Builder
@Entity
@Table(name = "THEORDER")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ORDERID")
    private Long orderId;

    @Column(name = "CUSTOMERID")
    private Long customerId;

    @Column(name = "NAMERECEIVER")
    private String nameReceiver;

    @Column(name = "PHONERECEIVER")
    private String phoneReceiver;

    @Column(name = "ADDRESS")
    private String address;

    @Column(name = "NOTE")
    private String note;

    @OneToOne
    @JoinColumn(name = "STATUSID")
    private Status status;

    @Column(name = "EMPLOYEEREVIEW")
    private Long employeeReview;

    @Column(name = "EMPLOYEEDELIVERY")
    private Long employeeDelivery;

    @OneToOne(mappedBy = "orderId",fetch = FetchType.EAGER)
    @JsonManagedReference
    private Bill bill;

    @OneToOne(mappedBy = "orderId",fetch = FetchType.LAZY)
    @JsonManagedReference
    private ReturnForm returnForm;
}