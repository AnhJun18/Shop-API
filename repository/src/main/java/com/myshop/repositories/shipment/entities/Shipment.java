package com.myshop.repositories.shipment.entities;

import com.myshop.repositories.Auditing;
import com.myshop.repositories.order.entities.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Builder
@Entity
@Table(name = "shipment")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Shipment extends Auditing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String shipCode;

    @Column(columnDefinition = "nvarchar(255)")
    private String nameReceiver;

    @Column(columnDefinition = "nvarchar(255)")
    private String province;

    @Column(columnDefinition = "nvarchar(255)")
    private String district;

    @Column(columnDefinition = "nvarchar(255)")
    private String ward;

    @Column(columnDefinition = "nvarchar(255)")
    private String address;

    @Column(name = "phone")
    private String phoneReceiver;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    public String getFullAddress(){
        return address+", "+ward+", "+district+", "+province;
    }
}
