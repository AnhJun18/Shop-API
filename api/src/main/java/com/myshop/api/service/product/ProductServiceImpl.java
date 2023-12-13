package com.myshop.api.service.product;

import com.myshop.api.base.CRUDBaseServiceImpl;
import com.myshop.api.base.ProcedureNames;
import com.myshop.api.payload.dtos.ProductDTO;
import com.myshop.api.payload.request.product.ProductPriceDetailRequest;
import com.myshop.api.payload.request.product.ProductPriceRequest;
import com.myshop.api.payload.request.product.ProductRequest;
import com.myshop.api.service.firebase.IImageService;
import com.myshop.common.http.ApiResponse;
import com.myshop.common.http.CodeStatus;
import com.myshop.common.http.ListResponse;
import com.myshop.repositories.category.entities.Category;
import com.myshop.repositories.category.repos.CategoryRepository;
import com.myshop.repositories.common.GlobalOption;
import com.myshop.repositories.product.entities.Product;
import com.myshop.repositories.product.entities.ProductCategory;
import com.myshop.repositories.product.repos.ProductRepository;
import com.myshop.repositories.product_category.repos.ProductCategoryRepository;
import com.myshop.repositories.user.entities.Employee;
import com.myshop.repositories.user.repos.EmployeeRepository;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureQuery;
import java.util.*;


@Transactional
@Service
public class ProductServiceImpl extends CRUDBaseServiceImpl<Product, ProductRequest, Product, Long> implements ProductService {

  private final ProductRepository productRepository;

  @Autowired
  ProductCategoryRepository productCategoryRepository;
  @Autowired
  IImageService imageService;
  @Autowired
  CategoryRepository categoryRepository;
  @Autowired
  EmployeeRepository employeeRepository;
  private final EntityManager entityManager;

  public ProductServiceImpl(ProductRepository productRepository, EntityManager entityManager) {
    super(Product.class, ProductRequest.class, Product.class, productRepository);
    this.productRepository = productRepository;
    this.entityManager = entityManager;
  }

  @Transactional
  @Override
  public ApiResponse<?> createProduct(@RequestBody ProductRequest productRequest) {
    String imageDefault;
    try {
      imageDefault = imageService.saveBase64String(productRequest.getPictures());

      Product newProduct = Product.builder()
              .linkImg(imageDefault)
              .name(productRequest.getName())
              .description(productRequest.getDescription())
              .build();
      productRepository.save(newProduct);

      for (String categoryCode : productRequest.getCategories()) {
        Category category = categoryRepository.findByCategoryCode(categoryCode);
        if (category == null)
          throw new Exception("Danh mục không tồn tại");
        else {
          productCategoryRepository.save(ProductCategory.builder()
                  .category(category)
                  .product(newProduct)
                  .build());
        }

      }
      return ApiResponse.builder().message("Tạo sản phẩm thành công!").status(200).data(null).build();
    } catch (Exception e) {
      return ApiResponse.builder().status(500).data(null).message("Lỗi upload file").build();
    }
  }

  @Transactional
  @Override
  public ApiResponse<?> updateProduct(Long id, ProductRequest productRequest) {
    String imagePr;
    try {
      Optional<Product> productSt = productRepository.findById(id);
      if (productSt.isPresent()) {
        imagePr = productRequest.getPictures().startsWith("http") ? productRequest.getPictures() : imageService.saveBase64String(productRequest.getPictures());
        productSt.get().setDescription(productRequest.getDescription());
        productSt.get().setName(productRequest.getName());
        productSt.get().setLinkImg(imagePr);
        productRepository.save(productSt.get());
        for (String categoryCode : productRequest.getCategories()) {
          Category category = categoryRepository.findByCategoryCode(categoryCode);
          if (category == null)
            throw new Exception("Danh mục không tồn tại");
          else {
            productCategoryRepository.save(ProductCategory.builder()
                    .category(category)
                    .product(productSt.get())
                    .build());
          }

        }
        return ApiResponse.builder().message("Tạo sản phẩm thành công!").status(200).data(null).build();

      } else {
        return ApiResponse.builder().message("Không tìm thấy sản phẩm!").status(504).data(null).build();
      }
    } catch (Exception e) {
      return ApiResponse.builder().status(500).data(null).message("Lỗi upload file").build();
    }
  }

  @Transactional
  @Override
  public ApiResponse<?> lockProduct(Long id) {
    Optional<Product> productSt = productRepository.findById(id);
    if (productSt.isPresent()) {
      productSt.get().setDeleted(true);
      productRepository.save(productSt.get());
      return ApiResponse.builder().message("Khóa sản phẩm thành công!").status(200).data(null).build();

    } else {
      return ApiResponse.builder().message("Không tìm thấy sản phẩm!").status(504).data(null).build();
    }
  }

  @Override
  public ApiResponse<?> getListProduct(String search, Integer page, Integer itemsPerPage, Long fromPrice, Long toPrice, List<String> types) {

    String typesCate = types == null ? null : String.join(",", types);
    StoredProcedureQuery query = entityManager.createStoredProcedureQuery(ProcedureNames.SP_PRODUCT_GetList)
            .registerStoredProcedureParameter("PAGE", Integer.class, ParameterMode.IN)
            .registerStoredProcedureParameter("PAGESIZE", Integer.class, ParameterMode.IN)
            .registerStoredProcedureParameter("SEARCH", String.class, ParameterMode.IN)
            .registerStoredProcedureParameter("FROMPRICE", Long.class, ParameterMode.IN)
            .registerStoredProcedureParameter("TOPRICE", Long.class, ParameterMode.IN)
            .registerStoredProcedureParameter("TYPES", String.class, ParameterMode.IN)
            .registerStoredProcedureParameter("TOTALITEM", Integer.class, ParameterMode.OUT);
    query.setParameter("PAGE", page);
    query.setParameter("PAGESIZE", itemsPerPage);
    query.setParameter("SEARCH", search);
    query.setParameter("FROMPRICE", fromPrice);
    query.setParameter("TOPRICE", toPrice);
    query.setParameter("TYPES", typesCate);

    query.execute();
    int totalItem = (int) query.getOutputParameterValue("TOTALITEM");
    List<Object[]> results = query.getResultList();
    List<Map<String, Object>> resMap = new ArrayList<>();
    for (Object[] it : results) {
      Map<String, Object> mapIt = new HashMap<>();
      mapIt.put("id", it[0]);
      mapIt.put("description", it[1]);
      mapIt.put("linkImg", it[2]);
      mapIt.put("name", it[3]);
      mapIt.put("viewed", it[4]);
      mapIt.put("isDeleted", it[5]);
      mapIt.put("price", it[6]);
      mapIt.put("promotionValue", it[7]);
      resMap.add(mapIt);
    }

    return ApiResponse.of(ListResponse.builder()
            .page(page)
            .totalItems(totalItem)
            .totalPages((int) Math.ceil(totalItem * 1.0 / itemsPerPage))
            .itemsPerPage(itemsPerPage)
            .items(resMap)
            .build());
  }

  @Override
  public ApiResponse<?> getById(Long id) {
    Optional<Product> product = productRepository.findById(id);
    if (!product.isPresent())
      return ApiResponse.fromErrorCode(CodeStatus.CODE_NOT_EXIST);
    List<ProductCategory> productCategories = productCategoryRepository.findProductCategoriesByProduct(product.get());
    List<String> categoryDTOs = new ArrayList<>();
    for (ProductCategory prCategory : productCategories) {
      categoryDTOs.add(prCategory.getCategory().getCategoryCode());
    }

    ProductDTO productDTO = new ProductDTO(
            product.get().getId(),
            product.get().getName(),
            product.get().getLinkImg(),
            product.get().getDescription(),
            product.get().getViewed(),
            categoryDTOs);
    return ApiResponse.of(productDTO);
  }

  @Override
  public ApiResponse<?> getDetailInventory(Long id) {
    StoredProcedureQuery query = entityManager.createStoredProcedureQuery(ProcedureNames.SP_PRODUCT_GetDetailProduct);
    query.registerStoredProcedureParameter("PRODUCTID", Long.class, ParameterMode.IN);
    query.setParameter("PRODUCTID", id);
    query.execute();

    List<Object[]> resultList = query.getResultList();
    List<Map<String, Object>> detailStocks = new ArrayList<>();
    Map<String, Object> resultMap = new HashMap<>();
    if (resultList != null && !resultList.isEmpty()) {
      Object[] info = resultList.get(0);
      resultMap.put("id", info[0]);
      resultMap.put("description", info[1]);
      resultMap.put("linkImg", info[2]);
      resultMap.put("name", info[3]);
      resultMap.put("promotionValue", info[6]);
      resultMap.put("price", info[7]);
    }

    StoredProcedureQuery query2 = entityManager.createStoredProcedureQuery(ProcedureNames.SP_PRODUCT_GetDetailInStock);
    query2.registerStoredProcedureParameter("PRODUCTID", Long.class, ParameterMode.IN);
    query2.setParameter("PRODUCTID", id);
    query2.execute();
    List<Object[]> resultList2 = query2.getResultList();
    for (Object[] ct : resultList2) {
      Map<String, Object> dtMap = new HashMap<>();
      dtMap.put("detailId", ct[0]);
      dtMap.put("color", ct[1]);
      dtMap.put("size", ct[2]);
      dtMap.put("inventory", ct[3]);
      detailStocks.add(dtMap);
    }
    resultMap.put("detailInventory", detailStocks);

    return ApiResponse.of(resultMap);


  }

  @Transactional
  @Override
  public ApiResponse<?> updatePrice(String emailEmp, ProductPriceRequest productPriceRequest) {
    Employee employee = employeeRepository.findByEmail(emailEmp).get();
      for (ProductPriceDetailRequest priceDetail : productPriceRequest.getList_order()) {
        productRepository.updatePrice(priceDetail.getProductId(), employee.getId(), productPriceRequest.getFromDate(), Long.valueOf(priceDetail.getNewPrice()));
      }
      return ApiResponse.builder().message("Điều chỉnh giá thành công!").status(200).data(null).build();
  }

  @Override
  public ApiResponse<?> getTopViewed() {
    StoredProcedureQuery query = entityManager.createStoredProcedureQuery(ProcedureNames.SP_PRODUCT_GetTopViewed);
    query.execute();
    List<Object[]> results = query.getResultList();

    List<Map<String, Object>> resultList = new ArrayList<>();
    for (Object[] row : results) {
      Map<String, Object> result = new HashMap<>();
      result.put("id", row[0]);
      result.put("name", row[1]);
      result.put("description", row[2]);
      result.put("linkImg", row[3]);
      result.put("price", row[5]);
      result.put("promotionValue", row[7]);
      resultList.add(result);
    }
    return ApiResponse.of(resultList);
  }

  @Override
  public ApiResponse<?> getBestSelling() {
    StoredProcedureQuery query = entityManager.createStoredProcedureQuery(ProcedureNames.SP_PRODUCT_GetBestSelling);
    query.execute();
    List<Object[]> results = query.getResultList();
    System.out.println(results.size());
    List<Map<String, Object>> resMap = new ArrayList<>();
    for (Object[] it : results) {
      Map<String, Object> mapIt = new HashMap<>();
      mapIt.put("id", it[0]);
      mapIt.put("description", it[1]);
      mapIt.put("linkImg", it[2]);
      mapIt.put("name", it[3]);
      mapIt.put("viewed", it[4]);
      mapIt.put("isDeleted", it[5]);
      mapIt.put("price", it[6]);
      mapIt.put("promotionValue", it[7]);
      resMap.add(mapIt);
    }
    return ApiResponse.of(resMap);
  }

  public List<Map<String, Object>> getSuggest(String infoProduct) {
    StoredProcedureQuery query = entityManager.createStoredProcedureQuery(ProcedureNames.SP_PRODUCT_GetSuggest)
            .registerStoredProcedureParameter("PRODUCTIDS", String.class, ParameterMode.IN);
    query.setParameter("PRODUCTIDS", infoProduct);
    query.execute();
    List<Object[]> results = query.getResultList();
    List<Map<String, Object>> resMap = new ArrayList<>();
    for (Object[] it : results) {
      Map<String, Object> mapIt = new HashMap<>();
      mapIt.put("id", it[0]);
      mapIt.put("description", it[1]);
      mapIt.put("linkImg", it[2]);
      mapIt.put("name", it[3]);
      mapIt.put("viewed", it[4]);
      mapIt.put("isDeleted", it[5]);
      mapIt.put("price", it[6]);
      mapIt.put("promotionValue", it[7]);
      resMap.add(mapIt);
    }
    return resMap ;
  }

  @Override
  public ApiResponse<?> getListProductOnPromotion() {
    StoredProcedureQuery query = entityManager.createStoredProcedureQuery(ProcedureNames.SP_PRODUCT_GetListOnPromotion);
    query.execute();
    List<Object[]> results = query.getResultList();
    List<Map<String, Object>> resMap = new ArrayList<>();
    for (Object[] it : results) {
      Map<String, Object> mapIt = new HashMap<>();
      mapIt.put("id", it[0]);
      mapIt.put("description", it[1]);
      mapIt.put("linkImg", it[2]);
      mapIt.put("name", it[3]);
      mapIt.put("viewed", it[4]);
      mapIt.put("isDeleted", it[5]);
      mapIt.put("price", it[6]);
      mapIt.put("promotionValue", it[7]);
      resMap.add(mapIt);
    }
    return ApiResponse.of(resMap);
  }

  @Override
  public ApiResponse<?> getOptionProduct() {
    StoredProcedureQuery query = entityManager.createStoredProcedureQuery("CBO_GetOption", GlobalOption.class);
    query.registerStoredProcedureParameter("TABLENAME", String.class, ParameterMode.IN);
    query.registerStoredProcedureParameter("COLUMNID", String.class, ParameterMode.IN);
    query.registerStoredProcedureParameter("COLUMNNAME", String.class, ParameterMode.IN);
    query.setParameter("TABLENAME", "PRODUCT");
    query.setParameter("COLUMNID", "PRODUCTID");
    query.setParameter("COLUMNNAME", "PRODUCTNAME");
    query.execute();
    List<GlobalOption> options = query.getResultList();

    return ApiResponse.of(options);
  }
}
