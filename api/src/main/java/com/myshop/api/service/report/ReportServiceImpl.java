package com.myshop.api.service.report;

import com.myshop.api.service.order.OrderService;
import com.myshop.repositories.order.repos.OrderDetailRepository;
import com.myshop.repositories.order.repos.OrderRepository;
import com.myshop.repositories.product.repos.ProductRepository;
import com.myshop.repositories.user.entities.Employee;
import com.myshop.repositories.user.repos.EmployeeRepository;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ReportServiceImpl implements ReportService {
    @Autowired
    OrderRepository orderRepository;

    @Autowired
    OrderDetailRepository orderDetailRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    AuditorAware auditorAware;
    @Autowired
    OrderService orderService;
    @Autowired
    EmployeeRepository employeeRepository;

    @Override
    public byte[] reportRevenueByDay(String fromDate,String toDate) throws IOException, JRException {
        Employee employee = employeeRepository.findByEmail(auditorAware.getCurrentAuditor().get().toString()).get();
        JasperReport jasperReport = JasperCompileManager.compileReport(new ClassPathResource("templates/report_day.jrxml").getInputStream());
        List<Map<String, Object>> list = (List<Map<String, Object>>) orderRepository.reportRevenueByDay(fromDate,toDate);

        if (list.size() == 0) {
            Map empty = new HashMap();
            empty.put("ngay", "không có dữ liệu");
            list.add(empty);
        }
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(list);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("fromDate", fromDate);
        parameters.put("toDate", toDate);
        parameters.put("employeeCreated", employee.getFirstName()+" "+employee.getLastName());
        return JasperExportManager.exportReportToPdf(JasperFillManager.fillReport(jasperReport, parameters, dataSource));
    }


    @Override
    public byte[] reportRevenueByOrder(String fromDate,String toDate) throws IOException, JRException {
        Employee employee = employeeRepository.findByEmail(auditorAware.getCurrentAuditor().get().toString()).get();
        JasperReport jasperReport = JasperCompileManager.compileReport(new ClassPathResource("templates/report_order.jrxml").getInputStream());
        List<Map<String, Object>> list = (List<Map<String, Object>>) orderRepository.reportRevenueByOrder(fromDate,toDate);
        if (list.size() == 0) {
            Map empty = new HashMap();
            empty.put("customerName", "không có dữ liệu");
            list.add(empty);
        }
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(list);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("fromDate", fromDate);
        parameters.put("toDate", toDate);
        parameters.put("employeeCreated", employee.getFirstName()+" "+employee.getLastName());
        return JasperExportManager.exportReportToPdf(JasperFillManager.fillReport(jasperReport, parameters, dataSource));
    }
    @Override
    public byte[] reportProfitByDay(String fromDate,String toDate) throws IOException, JRException {
        Employee employee = employeeRepository.findByEmail(auditorAware.getCurrentAuditor().get().toString()).get();
        JasperReport jasperReport = JasperCompileManager.compileReport(new ClassPathResource("templates/report_profit_day.jrxml").getInputStream());
        List<Map<String, Object>> list = (List<Map<String, Object>>) orderRepository.reportProfitByDay(fromDate,toDate);
        if (list.size() == 0) {
            Map empty = new HashMap();
            empty.put("customerName", "không có dữ liệu");
            list.add(empty);
        }
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(list);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("fromDate", fromDate);
        parameters.put("toDate", toDate);
        parameters.put("employeeCreated", employee.getFirstName()+" "+employee.getLastName());
        return JasperExportManager.exportReportToPdf(JasperFillManager.fillReport(jasperReport, parameters, dataSource));
    }
    @Override
    public byte[] reportProfitByProduct(String fromDate,String toDate) throws IOException, JRException {
        Employee employee = employeeRepository.findByEmail(auditorAware.getCurrentAuditor().get().toString()).get();
        JasperReport jasperReport = JasperCompileManager.compileReport(new ClassPathResource("templates/report_profit_product.jrxml").getInputStream());
        List<Map<String, Object>> list = (List<Map<String, Object>>) orderRepository.reportProfitByProduct(fromDate,toDate);
        if (list.size() == 0) {
            Map empty = new HashMap();
            empty.put("customerName", "không có dữ liệu");
            list.add(empty);
        }
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(list);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("fromDate", fromDate);
        parameters.put("toDate", toDate);
        parameters.put("employeeCreated", employee.getFirstName()+" "+employee.getLastName());
        return JasperExportManager.exportReportToPdf(JasperFillManager.fillReport(jasperReport, parameters, dataSource));
    }

    @Override
    public byte[] printBill(Long orderId) throws IOException, JRException {
        InputStream inputStream = new ClassPathResource("templates/bill.jrxml").getInputStream();
        JasperReport jasperReport = JasperCompileManager.compileReport(inputStream);
        Map<String, Object> dataOrder= (Map<String, Object>) orderService.getDetailById(orderId).getData();
        List<Map<String,Object>> detailsProductOrder = (List<Map<String, Object>>) dataOrder.get("details");

        List<Map<String, Object>> productList = new ArrayList<>();
        for (Map<String,Object> odt : detailsProductOrder) {
            Map<String, Object> product1 = new HashMap<>();
            product1.put("productName",odt.get("productName"));
            product1.put("quantity", odt.get("quantityOrder"));
            product1.put("unitPrice", odt.get("salePrice"));
            product1.put("amount",odt.get("amountOrder"));
            productList.add(product1);
        }

        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(productList);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("totalAmount", dataOrder.get("totalMoney"));
        parameters.put("orderId", orderId);
        parameters.put("mustPayMoney", dataOrder.get("totalMoney"));
        parameters.put("employeeDelivery", dataOrder.get("employeeDeliver"));
        parameters.put("customerPick", dataOrder.get("customerPicker"));
        parameters.put("vnpIPN", dataOrder.get("billCode"));
        parameters.put("orderDate", dataOrder.get("orderDate"));

        return JasperExportManager.exportReportToPdf(JasperFillManager.fillReport(jasperReport, parameters, dataSource));
    }

    @Override
    public byte[] reportInventory() throws IOException, JRException {
        JasperReport jasperReport = JasperCompileManager.compileReport(new ClassPathResource("templates/report_inventory.jrxml").getInputStream());
        Employee employee = employeeRepository.findByEmail(auditorAware.getCurrentAuditor().get().toString()).get();
        List<Map<String, Object>> list = (List<Map<String, Object>>) orderRepository.reportInventory();
        if (list.size() == 0) {
            Map empty = new HashMap();
            empty.put("product", "không có dữ liệu");
            list.add(empty);
        }
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(list);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("employeeCreated", employee.getFirstName()+" "+employee.getLastName());
        return JasperExportManager.exportReportToPdf(JasperFillManager.fillReport(jasperReport, parameters, dataSource));
    }

}