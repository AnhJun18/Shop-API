package com.myshop.api.service.product;

import com.myshop.api.base.CRUDBaseServiceImpl;
import com.myshop.api.payload.request.product.CategoryRequest;
import com.myshop.repositories.common.GlobalOption;
import com.myshop.api.service.firebase.IImageService;
import com.myshop.common.http.ApiResponse;
import com.myshop.common.http.ListResponse;
import com.myshop.repositories.category.entities.Category;
import com.myshop.repositories.category.repos.CategoryRepository;
import com.myshop.repositories.product.repos.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureQuery;
import java.io.IOException;
import java.util.List;


@Transactional
@Service
public class CategoryServiceImpl extends CRUDBaseServiceImpl<Category, CategoryRequest, Category, Long> implements CategoryService {

    private final CategoryRepository categoryRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    IImageService imageService;
    private final EntityManager entityManager;

    public CategoryServiceImpl(CategoryRepository categoryRepository, EntityManager entityManager) {
        super(Category.class, CategoryRequest.class, Category.class, categoryRepository);
        this.categoryRepository = categoryRepository;
        this.entityManager = entityManager;
    }

    @Transactional
    @Override
    public ApiResponse<?> createCategory(CategoryRequest categoryRequest) {
        boolean isExistCode = categoryRepository.existsByCategoryCode(categoryRequest.getCategoryCode());
        if (isExistCode)
            return ApiResponse.builder().status(405).data(null).message("Mã danh mục không được trùng").build();
        String imageDefault;
        try {
            imageDefault = imageService.saveBase64String(categoryRequest.getPictures().get(0));
        } catch (IOException e) {
            return ApiResponse.builder().status(500).data(null).message("Lỗi upload file").build();
        }
        Category newCategory = Category.builder()
                .pictures(imageDefault)
                .categoryName(categoryRequest.getCategoryName())
                .categoryCode(categoryRequest.getCategoryCode())
                .build();
        categoryRepository.save(newCategory);

        return ApiResponse.builder().status(200).data(null).message("Danh mục đã được thêm").build();
    }

    @Override
    public ApiResponse<?> getListCategory(String search, Integer page, Integer itemsPerPage) {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("CATEGORY_getList", Category.class);
        query.registerStoredProcedureParameter("PAGE", Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("PAGESIZE", Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("SEARCH", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("TOTALITEM", Integer.class, ParameterMode.OUT);

        query.setParameter("PAGE", page);
        query.setParameter("PAGESIZE", itemsPerPage);
        query.setParameter("SEARCH", search);

        query.execute();
        int totalItem = (int) query.getOutputParameterValue("TOTALITEM");
        List<Category> categories = query.getResultList();
        return ApiResponse.of(ListResponse.builder()
                .totalItems(totalItem)
                .itemsPerPage(itemsPerPage)
                .items(categories)
                .page(page)
                .build());
    }

    @Override
    public ApiResponse<?> getById(String code) {
        Category category = categoryRepository.findByCategoryCode(code);
        if (category == null || category.getCategoryName() == null)
            return ApiResponse.builder().status(404).data(null).message("Danh mục không tồn tại").build();
        return ApiResponse.of(category);
    }

    @Override
    public ApiResponse<?> getOptionCategory() {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("CBO_GetOption", GlobalOption.class);
        query.registerStoredProcedureParameter("TABLENAME", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("COLUMNID", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("COLUMNNAME", String.class, ParameterMode.IN);
        query.setParameter("TABLENAME", "CATEGORY");
        query.setParameter("COLUMNID", "CATEGORYCODE");
        query.setParameter("COLUMNNAME", "CATEGORYNAME");

        query.execute();
        List<GlobalOption> options = query.getResultList();

        return ApiResponse.of(options);
    }

}
