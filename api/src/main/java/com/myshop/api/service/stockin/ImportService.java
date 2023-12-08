package com.myshop.api.service.stockin;

import com.myshop.api.payload.dtos.ImportProductDto;
import com.myshop.api.payload.request.stockin.ImportRequest;
import com.myshop.api.payload.request.stockin.PreviewRequest;
import com.myshop.repositories.product.entities.Color;
import com.myshop.repositories.product.entities.Product;
import com.myshop.repositories.product.entities.ProductDetail;
import com.myshop.repositories.product.repos.ColorRepository;
import com.myshop.repositories.product.repos.ProductDetailRepository;
import com.myshop.repositories.product.repos.ProductRepository;
import com.myshop.repositories.product.repos.SizeRepository;
import com.myshop.repositories.stock.entites.StockIn;
import com.myshop.repositories.stock.entites.StockInDetail;
import com.myshop.repositories.stock.repos.StockInDetailRepository;
import com.myshop.repositories.stock.repos.StockInRepository;
import com.myshop.repositories.user.repos.CustomerRepository;
import com.myshop.repositories.user.repos.EmployeeRepository;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Transactional
@Service
public class ImportService {

  @Autowired
  StockInRepository stockInRepository;
  @Autowired
  StockInDetailRepository stockInDetailRepository;
  @Autowired
  ProductRepository productRepository;
  @Autowired
  ColorRepository colorRepository;
  @Autowired
  SizeRepository sizeRepository;
  @Autowired
  ProductDetailRepository productDetailRepository;
  @Autowired
  EmployeeRepository employeeRepository;

  private List<ImportProductDto> _getDataFromBase64(String base64Excel) throws IOException {
    String base64Data = base64Excel.split(",")[1];
    byte[] excelBytes = Base64.getDecoder().decode(base64Data);
    Map<String, Long> mapColor = new HashMap<>();
    Map<String, Long> mapSize = new HashMap<>();
    InputStream inputStream = new ByteArrayInputStream(excelBytes);
    List<ImportProductDto> objectList = null;
    XSSFWorkbook workbook = new XSSFWorkbook(inputStream);

    colorRepository.findAll().forEach((item)->{
      mapColor.put(item.getColorName(),item.getColorId());
    });

    sizeRepository.findAll().forEach((item)->{
      mapSize.put(item.getSizeName(),item.getSizeId());
    });

    XSSFSheet sheet = workbook.getSheetAt(0);
    Iterator<Row> rowIterator = sheet.iterator();
    objectList = new ArrayList<>();
    boolean isData = false;
    while (rowIterator.hasNext() && !isData) {
      Row row = rowIterator.next();
      if(row.getCell(0).getStringCellValue().equals("ID")){
        isData=true;
      }
    }
    while (rowIterator.hasNext()) {
      Row row = rowIterator.next();
      if (row.getCell(1) == null) break;
      ImportProductDto obj = new ImportProductDto();
      obj.setProductId((long) row.getCell(0).getNumericCellValue());
      obj.setProductName(row.getCell(1).getStringCellValue());
      obj.setColorId(mapColor.get(row.getCell(2).getStringCellValue()));
      obj.setColorName(row.getCell(2).getStringCellValue());
      obj.setSizeId(mapSize.get(row.getCell(3).getStringCellValue()));
      obj.setSizeName(row.getCell(3).getStringCellValue());
      obj.setQuantity((long) row.getCell(4).getNumericCellValue());
      obj.setPricesIn((long) row.getCell(5).getNumericCellValue());
      objectList.add(obj);
    }
    workbook.close();
    return objectList;
  }

  public List<ImportProductDto> previewFromExcel(PreviewRequest previewRequest) throws IOException {
    return _getDataFromBase64(previewRequest.getDataExcel());
  }

  public boolean importFromExcel(String userCreate, ImportRequest importRequest) throws Exception {
    List<ImportProductDto> data = _getDataFromBase64(importRequest.getDataExcel());
    Map<Long, Long> listQuantityMapper = new HashMap<>();
    Map<Long, Long> listPriceInMapper = new HashMap<>();
    Map<Long, ProductDetail> listDetailPr = new HashMap<>();
    for (ImportProductDto item : data) {
      Optional<Product> pr = productRepository.findById(item.getProductId());
      if (pr.isEmpty()) {
        throw new Exception(String.format("Mã sản phẩm %d không tồn tại!", item.getProductId()));
      }
      ProductDetail productDetail = productDetailRepository.findByProductAndColorAndSize(item.getProductId(), item.getColorId(), item.getSizeId()).orElse(null);
      if (productDetail == null) {
        productDetail = ProductDetail.builder()
                .color(item.getColorId())
                .product(item.getProductId())
                .size(item.getSizeId())
                .quantity(0L).build();
        productDetailRepository.save(productDetail);
      }
      listPriceInMapper.put(productDetail.getProduct(),item.getPricesIn());
      listQuantityMapper.put(productDetail.getId(), item.getQuantity() + productDetail.getQuantity());
      listDetailPr.put(productDetail.getId(), productDetail);
    }

    Optional<StockIn> stockInCheck = stockInRepository.findById(importRequest.getImportId());
    if (stockInCheck.isPresent()) {
      List<StockInDetail> listDetailOld = stockInDetailRepository.getAllByStockIn_StockInId(importRequest.getImportId());
      for (StockInDetail itemOld : listDetailOld) {
        Long tmp = listQuantityMapper.get(itemOld.getProductDetail().getId());
        if (tmp != null)
          listQuantityMapper.replace(itemOld.getProductDetail().getId(), tmp - itemOld.getQuantity());
        else {
          ProductDetail prNeedDelete = productDetailRepository.findById(itemOld.getProductDetail().getId()).orElse(null);
          prNeedDelete.setQuantity(prNeedDelete.getQuantity() - itemOld.getQuantity());
          productDetailRepository.save(prNeedDelete);
        }
      }
      stockInDetailRepository.deleteAllByStockIn_StockInId(stockInCheck.get().getStockInId());
      stockInRepository.delete(stockInCheck.get());
    }
    StockIn stockInNew = StockIn.builder().stockInId(importRequest.getImportId()).supplierId(null).employeeCreated(employeeRepository.findByEmail(userCreate).orElse(null).getId()).build();
    stockInRepository.save(stockInNew);

    listDetailPr.forEach((key, value) -> {
      ProductDetail tmp = value;
      tmp.setQuantity(listQuantityMapper.get(tmp.getId()));
      productDetailRepository.save(tmp);
      StockInDetail detailIn = StockInDetail.builder()
              .productDetail(value)
              .stockIn(stockInNew)
              .quantity(value.getQuantity())
              .pricesIn(listPriceInMapper.get(value.getProduct()))
              .build();
      stockInDetailRepository.save(detailIn);
    });
    return true;
  }


}
