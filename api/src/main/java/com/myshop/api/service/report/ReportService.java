package com.myshop.api.service.report;

import net.sf.jasperreports.engine.JRException;

import java.io.IOException;
import java.util.Date;

public interface ReportService {
    byte[] reportAllOrder(Date from, Date to, String name) throws IOException, JRException;
    byte[] reportMonthlyRevenue(Date date) throws IOException, JRException;
    byte[] reportProductRevenue(Date from, Date to)throws IOException, JRException;
}
