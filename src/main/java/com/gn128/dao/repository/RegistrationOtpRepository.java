package com.gn128.dao.repository;

import com.gn128.entity.RegistrationOtp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Author - rohit
 * Project - java-backend
 * Package - com.gn128.repository
 * Created_on - November 10 - 2024
 * Created_at - 14:42
 */

public interface RegistrationOtpRepository extends JpaRepository<RegistrationOtp, String> {
    Optional<RegistrationOtp> findByUserId(String userId);
}
