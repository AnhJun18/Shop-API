package com.myshop.repositories.promotion.entity;

import com.myshop.repositories.Auditing;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Instant;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "PROMOTION")
@Entity
public class Promotion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PROMOTIONID")
    private Long id;

    @Column(name = "PROMOTIONNAME")
    private String name;

    @Column(name = "DESCRIBE")
    private String description;

    @Column(name = "STARTDATE")
    private Instant startDate;

    @Column(name = "ENDDATE")
    private Instant endDate;

    @Column(name = "ISSTOPED")
    private Boolean isStoped;

}
