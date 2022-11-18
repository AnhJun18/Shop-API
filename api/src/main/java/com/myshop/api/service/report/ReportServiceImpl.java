package com.myshop.api.service.report;

import com.myshop.repositories.order.repos.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ReportServiceImpl implements ReportService {
    @Autowired
    OrderRepository orderRepository;

    @Override
    public byte[] reportAllOrder(Date from, Date to, String status) throws IOException, JRException {
        JasperReport jasperReport = JasperCompileManager.compileReport(new ClassPathResource("templates/report.jrxml").getInputStream());
        List<Map<String,Object>> orders= (List<Map<String, Object>>) orderRepository.findAllOrderToReport(from,to,status);
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(orders);
        Map<String, Object> parameters = new HashMap<>();
//        parameters.put("createdBy", "TuanLM");
        return JasperExportManager.exportReportToPdf(JasperFillManager.fillReport(jasperReport, parameters, dataSource));
    }
}