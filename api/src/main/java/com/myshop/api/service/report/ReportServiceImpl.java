package com.myshop.api.service.report;

import com.myshop.repositories.order.repos.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigInteger;
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
        JasperReport jasperReport = JasperCompileManager.compileReport(new ClassPathResource("templates/report_order.jrxml").getInputStream());
        List<Map<String,Object>> orders= (List<Map<String, Object>>) orderRepository.findAllOrderToReport(from,to,status);
        if(orders.size()==0){
            Map empty= new HashMap();
            empty.put("id",new Long("0"));
            orders.add(empty);
        }
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(orders);
        Map<String, Object> parameters = new HashMap<>();
        return JasperExportManager.exportReportToPdf(JasperFillManager.fillReport(jasperReport, parameters, dataSource));
    }

    @Override
    public byte[] reportMonthlyRevenue(Date month) throws IOException, JRException {
        JasperReport jasperReport = JasperCompileManager.compileReport(new ClassPathResource("templates/report_monthly.jrxml").getInputStream());
        List<Map<String,Object>> list= (List<Map<String, Object>>) orderRepository.reportMonthlyRevenue(month.getMonth()+1,month.getYear()+1900);
        if(list.size()==0){
            Map empty= new HashMap();
            empty.put("ngay",0);
            list.add(empty);
        }
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(list);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("month", month);
        return JasperExportManager.exportReportToPdf(JasperFillManager.fillReport(jasperReport, parameters, dataSource));
    }

    @Override
    public byte[] reportProductRevenue(Date from, Date to) throws IOException, JRException {
        JasperReport jasperReport = JasperCompileManager.compileReport(new ClassPathResource("templates/report_product.jrxml").getInputStream());
        List<Map<String,Object>> list= (List<Map<String, Object>>) orderRepository.reportProductRevenue(from.toInstant(),to.toInstant());
        if(list.size()==0){
            Map empty= new HashMap();
            empty.put("id",new BigInteger("0"));
            list.add(empty);
        }
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(list);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("from", from);
        parameters.put("to", to);
        return JasperExportManager.exportReportToPdf(JasperFillManager.fillReport(jasperReport, parameters, dataSource));
    }
}