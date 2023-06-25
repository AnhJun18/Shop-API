package com.myshop.repositories.warehouse.entities;

import com.myshop.repositories.Auditing;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Builder
@Entity
@Table(name = "warehouse_receipt")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseReceipt extends Auditing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

}