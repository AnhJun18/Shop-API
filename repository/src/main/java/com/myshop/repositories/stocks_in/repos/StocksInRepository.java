package com.myshop.repositories.stocks_in.repos;

import com.myshop.repositories.stocks_in.entities.StocksIn;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Map;

@Repository
public interface StocksInRepository extends CrudRepository<StocksIn, Long>, JpaSpecificationExecutor<StocksIn> {
    @Query("SELECT u.id as id, u.createdDate as createdDate, sum(k.amount) as totalAmount, " +
            " sum(k.amount*k.costPrices) as totalMoney " +
            "FROM StocksIn u left join  StocksInDetail k " +
            "on u.id=k.stocksIn.id" +
            " group by u.id,u.createdDate")
    Iterable<Map<String, Object>> getAllReceipt(Instant from, Instant to);
}
