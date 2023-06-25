package com.myshop.repositories.stocks.repos;

import com.myshop.repositories.stocks.entities.StocksDetail;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;

@Repository
public interface StocksDetailRepository extends CrudRepository<StocksDetail, Long> , JpaSpecificationExecutor<StocksDetail> {
    Optional<StocksDetail> findProductDetailByInfoProduct_IdAndSizeAndAndColor(Long productID, String size, String Color);


    @Query(value = "exec getStockByProductId :id ",nativeQuery = true)
    Iterable<Map<String,Object>> findAllByInfoProduct_Id(@Param("id") Long id);
}
