package com.myshop.repositories.chatbot.repos;

import com.myshop.repositories.chatbot.entities.DataCollect;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DataCollectRepository extends CrudRepository<DataCollect, Long>, JpaSpecificationExecutor<DataCollect> {

}
