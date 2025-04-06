package com.myshop.repositories.product_category.repos;

import com.myshop.repositories.product.entities.Product;
import com.myshop.repositories.product_category.entities.ProductCategory;
import com.myshop.repositories.product_category.entities.ProductCategoryKey;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ProductCategoryRepository extends CrudRepository<ProductCategory, ProductCategoryKey> , JpaSpecificationExecutor<ProductCategory> {
    List<ProductCategory> findProductCategoriesByProduct(Product product);
}
