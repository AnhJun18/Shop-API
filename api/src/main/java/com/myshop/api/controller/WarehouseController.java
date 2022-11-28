package com.myshop.api.controller;


import com.myshop.api.payload.request.product.AddProductDetailRequest;
import com.myshop.api.payload.response.product.ProductDetailResponse;
import com.myshop.api.service.warehouse.WarehouseService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
    public Mono<Iterable<Map<String,Object>>> getHistory(@RequestParam(name = "from",required = false,defaultValue = "2022-01-01") @DateTimeFormat(pattern="yyyy-MM-dd") Date fromDate,
                                                     @RequestParam(name = "to",required = false )@DateTimeFormat(pattern="yyyy-MM-dd") Date toDate) {
        return Mono.just(warehouseService.getHistory(fromDate,toDate==null?new Date():toDate));
    }


}
