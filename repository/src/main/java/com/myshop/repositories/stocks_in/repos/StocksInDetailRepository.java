
package com.myshop.repositories.stocks_in.repos;

import com.myshop.repositories.stocks_in.entities.StocksInDetail;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StocksInDetailRepository extends CrudRepository<StocksInDetail, Long>, JpaSpecificationExecutor<StocksInDetail> {

}
