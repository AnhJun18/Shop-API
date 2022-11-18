package com.myshop.repositories.order.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.myshop.repositories.Auditing;
import com.myshop.repositories.user.entities.UserInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Builder
@Entity
@Table(name = "the_order")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order extends Auditing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    private UserInfo userInfo;

    private String address;

    private String phoneReceiver;

    private String note;

    private Double feeShip;

    @OneToOne
    private Status status;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "order")
    @JsonManagedReference
    private List<OrderDetail> orderDetails;

    public Order copy() {
        Order newOrder = new Order();
        newOrder.userInfo = null;
        newOrder.setAddress(this.address);
        newOrder.setNote(this.note);
        newOrder.setFeeShip(this.feeShip);
        newOrder.setStatus(this.status);
        newOrder.setOrderDetails(this.orderDetails);
        return  newOrder;
    }

}