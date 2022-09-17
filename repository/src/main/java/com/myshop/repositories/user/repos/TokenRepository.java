package com.myshop.repositories.user.repos;

import com.myshop.repositories.user.entities.Token;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends CrudRepository<Token, Long> {

    @Query("SELECT t FROM Token t WHERE t.userId = :userId")
    Token findByUserId(@Param("userId") String userId);

    @Query("SELECT t FROM Token t WHERE t.tokenId = :tokenId")
    Token findByTokenId(@Param("tokenId") String tokenId);

    @Modifying
    @Query("DELETE FROM Token t WHERE t.tokenId = :tokenId")
    int deleteTokenByTokenId(@Param("tokenId") String tokenId);
}
