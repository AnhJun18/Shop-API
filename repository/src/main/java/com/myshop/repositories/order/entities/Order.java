package com.myshop.repositories.order.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.myshop.repositories.Auditing;
import com.myshop.repositories.payment.entities.Payment;
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

    @Column(columnDefinition = "nvarchar(255)")
    private String nameReceiver;

    @Column(columnDefinition = "nvarchar(255)")
    private String address;

    private String phoneReceiver;

    @Column(columnDefinition = "nvarchar(255)")
    private String note;


    @Column(columnDefinition = "nvarchar(255)")
    private String province;

    @Column(columnDefinition = "nvarchar(255)")
    private String district;

    @Column(columnDefinition = "nvarchar(255)")
    private String ward;

    private Double feeShip;

    @OneToOne
    private Status status;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private Payment payment;

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

    public Integer getTotalPrices(){
        Double total= 0.0;
        for (OrderDetail o: this.getOrderDetails() ) {
            total+=o.getPrices()*o.getAmount();
        }
        total+=this.feeShip;
        return total.intValue();
    }

}