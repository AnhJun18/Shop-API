package com.myshop.api.controller;


import com.myshop.api.payload.request.product.AddProductDetailRequest;
import com.myshop.api.payload.response.product.ProductDetailResponse;
import com.myshop.api.service.warehouse.WarehouseService;
import com.myshop.repositories.order.entities.WarehouseReceipt;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;

/**
 * @author Anh Jun
 * @author Anh Jun
 */

@Slf4j
@RestController
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/api/warehouse")
public class WarehouseController {

    @Autowired
    WarehouseService warehouseService;

    @PostMapping(value = "/import")
    public Mono<ProductDetailResponse> createDetailProduct(@RequestBody List<AddProductDetailRequest> detailRequest) throws IOException {
        return Mono.just(warehouseService.importWarehouse(detailRequest));
    }

    @GetMapping(value = "/history_import")
    public Mono<List<WarehouseReceipt>> getHistory() {
        return Mono.just(warehouseService.getHistory());
    }


}
