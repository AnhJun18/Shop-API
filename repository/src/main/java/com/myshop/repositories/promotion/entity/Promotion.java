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
@Table(name = "promotion")
@Entity
public class Promotion extends Auditing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "nvarchar(255)")
    private String name;

    private Integer value;

    @Column(columnDefinition = "nvarchar(255)")
    private String description;

    private Instant startDate;

    private Instant endDate;

}
