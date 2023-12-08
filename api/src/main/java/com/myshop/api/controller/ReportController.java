package com.myshop.api.controller;

import com.myshop.api.service.report.ReportService;
import com.myshop.common.http.ApiResponse;
import com.myshop.common.http.CodeStatus;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Slf4j
@RestController
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("api/report")
public class ReportController {
    @Autowired
    ReportService service;

    @GetMapping(value = "/revenue_byDay")
    public ApiResponse<?> reportRevenueByDay(@RequestParam(required = false) String fromDate,
                                             @RequestParam(required = false) String toDate) {
        try {
            return ApiResponse.builder()
                    .status(200)
                    .data(service.reportRevenueByDay(fromDate, toDate)).build();
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.fromErrorCode(CodeStatus.INTERNAL_ERROR);
        }
    }

    @GetMapping(value = "/revenue_byOrder")
    public ApiResponse<?> reportRevenueByOrderreportRevenueByOrder(@RequestParam(required = false) String fromDate,
                                                                   @RequestParam(required = false) String toDate) {
        try {
            return ApiResponse.builder()
                    .status(200)
                    .data(service.reportRevenueByOrder(fromDate, toDate)).build();
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.fromErrorCode(CodeStatus.INTERNAL_ERROR);
        }
    }

    @GetMapping(value = "/profit_byDay")
    public ApiResponse<?> reportProfitByDay(@RequestParam(required = false) String fromDate,
                                                                   @RequestParam(required = false) String toDate) {
        try {
            return ApiResponse.builder()
                    .status(200)
                    .data(service.reportProfitByDay(fromDate, toDate)).build();
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.fromErrorCode(CodeStatus.INTERNAL_ERROR);
        }
    }

    @GetMapping(value = "/profit_byProduct")
    public ApiResponse<?> reportProfitByProduct(@RequestParam(required = false) String fromDate,
                                                                   @RequestParam(required = false) String toDate) {
        try {
            return ApiResponse.builder()
                    .status(200)
                    .data(service.reportProfitByProduct(fromDate, toDate)).build();
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.fromErrorCode(CodeStatus.INTERNAL_ERROR);
        }
    }

    @GetMapping(value = "/bill/{orderId}")
    public ApiResponse<?> printBill(@PathVariable Long orderId) {
        try {
            return ApiResponse.builder()
                    .status(200)
                    .data(service.printBill(orderId)).build();
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.fromErrorCode(CodeStatus.INTERNAL_ERROR);
        }
    }

    @GetMapping(value = "/inventory")
    public ApiResponse<?> reportInventory() {
        try {
            return ApiResponse.builder()
                    .status(200)
                    .data(service.reportInventory()).build();
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.fromErrorCode(CodeStatus.INTERNAL_ERROR);
        }
    }


}