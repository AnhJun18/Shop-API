package com.myshop.api.service.supplier;

import com.google.api.client.util.IOUtils;
import com.myshop.api.payload.dtos.RequestSupplierDto;
import com.myshop.api.payload.request.stockin.RequestSupplierRequest;
import com.myshop.api.payload.request.system.ColorRequest;
import com.myshop.api.payload.request.system.SizeRequest;
import com.myshop.common.http.ApiResponse;
import com.myshop.repositories.common.GlobalOption;
import com.myshop.repositories.product.entities.Color;
import com.myshop.repositories.product.entities.Size;
import com.myshop.repositories.product.repos.ColorRepository;
import com.myshop.repositories.product.repos.SizeRepository;
import com.myshop.repositories.supplier.entities.Supplier;
import com.myshop.repositories.supplier.repos.SupplierRepository;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureQuery;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

@Transactional
@Service
public class SupplierServiceImpl implements SupplierService {
  private final EntityManager entityManager;

  @Autowired
  ColorRepository colorRepository;
  @Autowired
  SupplierRepository supplierRepository;
  @Autowired
  SizeRepository sizeRepository;

  public SupplierServiceImpl(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  @Override
  public ByteArrayOutputStream exportExcelRequestSupplier(RequestSupplierRequest exportRequest) throws Exception {
    String templatePath = "templates/TemplateExport.xlsx";
    Supplier supplier = supplierRepository.findById(Long.parseLong(exportRequest.getSupplierId())).get();
    ClassPathResource resource = new ClassPathResource(templatePath);
    InputStream inputStream = resource.getInputStream();
    Workbook workbook = new XSSFWorkbook(inputStream);
    Sheet sheet = workbook.getSheetAt(0);

    DataFormat format = workbook.createDataFormat();
    CellStyle currencyStyle = workbook.createCellStyle();
    currencyStyle.setDataFormat(format.getFormat("#,##0 ₫"));

    List<RequestSupplierDto> productList = exportRequest.getDataRequestSupplier();
    boolean isData = false;
    int rowCount = 0;
    Iterator<Row> rowIterator = sheet.iterator();
    while (rowIterator.hasNext() && !isData) {
      Row row = rowIterator.next();
      rowCount++;
      if (row.getCell(0).getStringCellValue().equals("ID")) {
        isData = true;
      } else if (row.getCell(0).equals("Tên nhà cung cấp")) {
        row.createCell(2).setCellValue(supplier.getSupplierName());
      }
    }
    sheet.shiftRows(++rowCount, sheet.getLastRowNum(), productList.size(), true, false);

    Long totalMoney = 0L;
    for (RequestSupplierDto product : productList) {
      Row row = sheet.createRow(rowCount++);
      row.createCell(0).setCellValue(product.getProductId());
      row.createCell(1).setCellValue(product.getProductName());
      row.createCell(2).setCellValue(product.getColor());
      row.createCell(3).setCellValue(product.getSize());
      row.createCell(4).setCellValue(product.getQuantity());
      row.createCell(5).setCellValue(product.getPrice());
      Long tmpMoney = product.getPrice() * product.getQuantity();
      totalMoney += tmpMoney;
      row.createCell(6).setCellValue(tmpMoney);
      row.getCell(5).setCellStyle(currencyStyle);
      row.getCell(6).setCellStyle(currencyStyle);
    }

    Row rowTotal = sheet.getRow(++rowCount);
    rowTotal.createCell(6).setCellValue(totalMoney);
    rowTotal.getCell(6).setCellStyle(currencyStyle);
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    IOUtils.copy(inputStream, outputStream);
    workbook.write(outputStream);
    workbook.close();

    return outputStream;
  }

  @Override
  public ApiResponse<?> getOptionSupplier() {
    StoredProcedureQuery query = entityManager.createStoredProcedureQuery("CBO_GetOption", GlobalOption.class);
    query.registerStoredProcedureParameter("TABLENAME", String.class, ParameterMode.IN);
    query.registerStoredProcedureParameter("COLUMNID", String.class, ParameterMode.IN);
    query.registerStoredProcedureParameter("COLUMNNAME", String.class, ParameterMode.IN);
    query.setParameter("TABLENAME", "SUPPLIER");
    query.setParameter("COLUMNID", "SUPPLIERID");
    query.setParameter("COLUMNNAME", "SUPPLIERNAME");
    query.execute();
    List<GlobalOption> options = query.getResultList();

    return ApiResponse.of(options);
  }

  @Override
  public ApiResponse<?> getOptionColor() {
    StoredProcedureQuery query = entityManager.createStoredProcedureQuery("CBO_GetOption", GlobalOption.class);
    query.registerStoredProcedureParameter("TABLENAME", String.class, ParameterMode.IN);
    query.registerStoredProcedureParameter("COLUMNID", String.class, ParameterMode.IN);
    query.registerStoredProcedureParameter("COLUMNNAME", String.class, ParameterMode.IN);
    query.setParameter("TABLENAME", "COLOR");
    query.setParameter("COLUMNID", "COLORID");
    query.setParameter("COLUMNNAME", "COLORNAME");
    query.execute();
    List<GlobalOption> options = query.getResultList();

    return ApiResponse.of(options);
  }

  @Override
  public ApiResponse<?> getOptionSize() {
    StoredProcedureQuery query = entityManager.createStoredProcedureQuery("CBO_GetOption", GlobalOption.class);
    query.registerStoredProcedureParameter("TABLENAME", String.class, ParameterMode.IN);
    query.registerStoredProcedureParameter("COLUMNID", String.class, ParameterMode.IN);
    query.registerStoredProcedureParameter("COLUMNNAME", String.class, ParameterMode.IN);
    query.setParameter("TABLENAME", "SIZE");
    query.setParameter("COLUMNID", "SIZEID");
    query.setParameter("COLUMNNAME", "SIZENAME");
    query.execute();
    List<GlobalOption> options = query.getResultList();

    return ApiResponse.of(options);
  }

  @Override
  public ApiResponse<?> getAllColor() {
    return ApiResponse.of(colorRepository.findAll());
  }

  @Override
  public ApiResponse<?> getAllSize() {
    return ApiResponse.of(sizeRepository.findAll());
  }

  @Override
  public ApiResponse<?> createSize(SizeRequest sizeRequest) {
    Size newSize = Size.builder().sizeName(sizeRequest.getSizeName()).build();
    sizeRepository.save(newSize);
    return ApiResponse.of(newSize);
  }

  @Override
  public ApiResponse<?> createColor(ColorRequest colorRequest) {
    Color newColor = Color.builder().colorName(colorRequest.getColorName()).colorCode(colorRequest.getColorCode()).build();
    colorRepository.save(newColor);
    return ApiResponse.of(newColor);
  }

}
