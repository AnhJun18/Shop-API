package com.myshop.api.service.report;

import net.sf.jasperreports.engine.JRException;

import java.io.IOException;

public interface ReportService {
    byte[] reportRevenueByDay(String fromDate, String toDate) throws IOException, JRException;
    byte[] reportRevenueByOrder(String fromDate, String toDate) throws IOException, JRException;
    byte[] printBill(Long orderID) throws IOException, JRException;
}