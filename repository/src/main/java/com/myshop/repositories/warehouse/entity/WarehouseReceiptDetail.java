package com.myshop.repositories.warehouse.entity;

import com.myshop.repositories.order.entities.WarehouseReceipt;
import com.myshop.repositories.product.entities.ProductDetail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Builder
@Entity
@Table(name = "warehouse_receipt_detail")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseReceiptDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "warehouse_receipt_id")
    private WarehouseReceipt warehouseReceipt;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private ProductDetail productDetail;

    private Long amount;

    private Double costPrices;

}
