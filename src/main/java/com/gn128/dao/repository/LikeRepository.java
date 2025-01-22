package com.gn128.dao.repository;

import com.gn128.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Author - rohit
 * Project - java-backend
 * Package - com.gn128.dao.repository
 * Created_on - December 05 - 2024
 * Created_at - 19:45
 */

public interface LikeRepository extends JpaRepository<Like, String> {
    List<Like> findAllByUserId(String userId);
}
