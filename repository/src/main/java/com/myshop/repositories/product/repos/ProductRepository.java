package com.myshop.repositories.product.repos;

import com.myshop.repositories.product.entities.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public interface ProductRepository extends CrudRepository<Product, Long> , JpaSpecificationExecutor<Product> {

    @Query("FROM Product u" +
            " where u.deleteFlag = false " +
            "order by u.category.id ASC ")
    Iterable<Product> findAllSortCategory();

    @Query(" SELECT  u.id as id, u.name as name,u.describe as describe,u.category.name as category, u.deleteFlag as status ," +
            " u.linkImg as linkImg, u.price as price, u.sold as sold, sum(k.current_number) as quantityInStock" +
            " FROM Product  u left join ProductDetail k on u.id = k.infoProduct.id" +
            " group by u.id,  u.name, u.describe,u.category.name, " +
            " u.linkImg ,u.price, u.sold, u.deleteFlag "
    )
    Page<Map<String,Object>> getListProductPaging(Pageable pageable);

    Iterable<Product> findAllByCategory_Name(String nameCategory);

    Iterable<Product> findAllByCategory_NameAndDeleteFlag(String nameCategory,boolean deleteFlag);

    Iterable<Product> findAllByTag_Name(String tag);

    @Query(" from Product p where p.name like %:name%" +
            " and p.deleteFlag =false " )
    Iterable<Product> searchProductByName(@Param("name") String name);
}
