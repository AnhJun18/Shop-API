package com.myshop.repositories.user.repos;

import com.myshop.repositories.user.entities.Account;
import com.myshop.repositories.user.entities.ForgotPassword;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ForgotPasswordRepository extends CrudRepository<ForgotPassword, Long> {
    ForgotPassword findByVerifyCode(String verifyCode);
    ForgotPassword findForgotPasswordByEmail(String email);
}
