package com.myshop.repositories.user.repos;

import com.myshop.repositories.user.entities.Token;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends CrudRepository<Token, String> {


    @Query("SELECT t FROM Token t WHERE t.tokenId = :tokenId")
    Token findByTokenId(@Param("tokenId") String tokenId);

}
