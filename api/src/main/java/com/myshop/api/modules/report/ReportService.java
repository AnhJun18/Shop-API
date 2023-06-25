package com.myshop.api.modules.report;

import net.sf.jasperreports.engine.JRException;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ReportService {
    byte[] reportAllOrder(Date from, Date to, String name) throws IOException, JRException;
    byte[] reportMonthlyRevenue(Date date) throws IOException, JRException;
    byte[] reportProductRevenue(Date from, Date to)throws IOException, JRException;
    List<Map<String, Object>> getRevenueInYear(String year);
}
