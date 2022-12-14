package com.myshop.repositories.chatbox.repos;

import com.myshop.repositories.chatbox.entities.Tags;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends CrudRepository<Tags, Long>, JpaSpecificationExecutor<Tags> {
    Tags findByName(String name);
}
