package com.gn128.dao.repository;

import com.gn128.entity.UserAuth;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Author - rohit
 * Project - java-backend
 * Package - com.gn128.repository
 * Created_on - November 10 - 2024
 * Created_at - 12:33
 */

public interface UserAuthRepository extends JpaRepository<UserAuth, String> {

    Optional<UserAuth> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<UserAuth> findByUsername(String username);
}
