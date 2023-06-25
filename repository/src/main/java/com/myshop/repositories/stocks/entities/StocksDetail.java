package com.myshop.repositories.stocks.entities;

import com.myshop.repositories.product.entities.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Builder
@Entity
@Table(name = "stock_detail",
        uniqueConstraints =@UniqueConstraint(columnNames = {"product", "size","color"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StocksDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.EAGER)
//    @JsonBackReference
    @JoinColumn(name = "product")
    private Product infoProduct;

    @Column(nullable = false)
    private String size;

    @Column(columnDefinition = "nvarchar(255)",nullable = false)
    private String color;

    private Long current_number;

}
