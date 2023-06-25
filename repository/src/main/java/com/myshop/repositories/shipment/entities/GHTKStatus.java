package com.myshop.repositories.shipment.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Builder
@Entity
@Table(name = "status_ghtk")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GHTKStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer code;

    @Column(columnDefinition = "nvarchar(255)")
    private String name;

}