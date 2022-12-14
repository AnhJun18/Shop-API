package com.myshop.repositories.product.entities;

import com.myshop.repositories.chatbot.entities.Tags;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Builder
@Entity
@Table(name = "product")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "nvarchar(255)")
    private String name;

    private String linkImg;

    @ManyToOne(fetch = FetchType.EAGER)
    private Category category;

    @Column(columnDefinition = "nvarchar(255)")
    private String describe;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = true)
    private Tags tag;

    private Double price;

    private Long sold;

    private boolean deleteFlag;

}
