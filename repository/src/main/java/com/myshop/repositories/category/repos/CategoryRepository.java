package com.myshop.repositories.category.repos;

import com.myshop.repositories.category.entities.Category;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CategoryRepository extends CrudRepository<Category, String> , JpaSpecificationExecutor<Category> {
    Category findByCategoryName(String name);

    Category findByCategoryCode(String code);

    boolean existsByCategoryCode(String code);

}
