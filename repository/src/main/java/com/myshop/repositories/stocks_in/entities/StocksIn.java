package com.myshop.repositories.stocks_in.entities;

import com.myshop.repositories.Auditing;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Builder
@Entity
@Table(name = "stock_in")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StocksIn extends Auditing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

}