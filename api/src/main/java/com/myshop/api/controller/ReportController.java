package com.myshop.api.controller;

import com.myshop.api.service.report.ReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@Slf4j
@RestController
@RequestMapping("api/report")
public class ReportController {
    @Autowired
    ReportService service;

    @GetMapping(value = "/list-order", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> getReport(@RequestParam(name = "from",required = false,defaultValue = "2022-01-01") @DateTimeFormat(pattern="yyyy-MM-dd") Date fromDate,
                                            @RequestParam(name = "to",required = false )@DateTimeFormat(pattern="yyyy-MM-dd") Date toDate,
                                            @RequestParam(name = "status",required = false ) String status) {
        try {
            return new ResponseEntity<byte[]>(service.reportAllOrder(fromDate,toDate==null?new Date():toDate,status), HttpStatus.OK);
        }
        catch(Exception e) {
            e.printStackTrace();
            return new ResponseEntity<byte[]>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/monthly_revenue", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> reportMonthlyRevenue(@RequestParam(name = "month",required = true) @DateTimeFormat(pattern="yyyy-MM") Date month) {
        try {
            return new ResponseEntity<byte[]>(service.reportMonthlyRevenue(month), HttpStatus.OK);
        }
        catch(Exception e) {
            e.printStackTrace();
            return new ResponseEntity<byte[]>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/product_revenue", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> reportProductRevenue(@RequestParam(name = "from", required = false, defaultValue = "2022-01-01") @DateTimeFormat(pattern = "yyyy-MM-dd") Date fromDate,
                                                       @RequestParam(name = "to", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date toDate) {
        try {
            return new ResponseEntity<byte[]>(service.reportProductRevenue(fromDate, toDate == null ? new Date():toDate), HttpStatus.OK);
        }
        catch(Exception e) {
            e.printStackTrace();
            return new ResponseEntity<byte[]>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}