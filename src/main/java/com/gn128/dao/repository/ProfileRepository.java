package com.gn128.dao.repository;

import com.gn128.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Author - rohit
 * Project - java-backend
 * Package - com.gn128.repository
 * Created_on - December 03 - 2024
 * Created_at - 20:15
 */

public interface ProfileRepository extends JpaRepository<Profile, String> {

    Optional<Profile> findByUserId(String userId);

    void deleteByUserId(String userId);
}
