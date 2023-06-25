package com.myshop.repositories.stocks_in.entities;

import com.myshop.repositories.stocks.entities.StocksDetail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Builder
@Entity
@Table(name = "stock_in_detail")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StocksInDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "stocks_in_id")
    private StocksIn stocksIn;

    @ManyToOne
    @JoinColumn(name = "stock_id")
    private StocksDetail stocksDetail;

    private Long amount;

    private Integer costPrices;

}
