package com.myshop.api.service.report;

import net.sf.jasperreports.engine.JRException;

import java.io.IOException;

public interface ReportService {
    public byte[] reportAllOrder() throws IOException, JRException;
}
