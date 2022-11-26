package com.myshop.repositories.product.repos;

import com.myshop.repositories.product.entities.Category;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends CrudRepository<Category, Long> , JpaSpecificationExecutor<Category> {
    Category findByName(String name);

}
