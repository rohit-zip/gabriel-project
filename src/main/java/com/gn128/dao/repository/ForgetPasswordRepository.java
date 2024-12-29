package com.gn128.dao.repository;


import com.gn128.entity.ForgetPassword;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ForgetPasswordRepository extends JpaRepository<ForgetPassword, String> {

    Optional<ForgetPassword> findByEmail(String email);
    Optional<ForgetPassword> findByUserId(String userId);
}
