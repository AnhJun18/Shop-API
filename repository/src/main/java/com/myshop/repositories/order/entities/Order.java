package com.myshop.repositories.order.entities;

import com.myshop.repositories.user.entities.UserInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.Instant;

@Builder
@Entity
@Table(name = "the_order")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    private UserInfo userInfo;

    private String address;

    private String note;

    private Double feeShip;

    @OneToOne
    private Status status;

    @CreatedDate
    protected Instant createdDate;
}