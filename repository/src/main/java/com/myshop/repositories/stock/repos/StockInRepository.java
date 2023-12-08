package com.myshop.repositories.stock.repos;

import com.myshop.repositories.stock.entites.StockIn;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface StockInRepository extends CrudRepository<StockIn, Long>, JpaSpecificationExecutor<StockIn> {
  @Override
  Optional<StockIn> findById(Long aLong);
}
