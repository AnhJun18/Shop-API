package com.myshop.repositories.stock.repos;

import com.myshop.repositories.stock.entites.StockInDetail;
import com.myshop.repositories.stock.entites.StockInDetailKey;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface StockInDetailRepository extends CrudRepository<StockInDetail, StockInDetailKey>, JpaSpecificationExecutor<StockInDetail> {

  List<StockInDetail> getAllByStockIn_StockInId(Long stockIn);

  void deleteAllByStockIn_StockInId(Long stockIn);
}
