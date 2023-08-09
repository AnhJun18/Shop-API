package com.myshop.repositories.category.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Builder
@Entity
@Table(name = "CATEGORY")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    @Id
    @Column(name = "CATEGORYCODE")
    private String categoryCode;

    @Column(name = "CATEGORYIMAGE")
    private String pictures;

    @Column(name = "CATEGORYNAME")
    private String categoryName;

}
