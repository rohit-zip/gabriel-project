package com.gn128.dao.repository;

/*
  Developer: Rohit Parihar
  Project: gabriel-project
  GitHub: github.com/rohit-zip
  File: UserStatusRepository
 */

import com.gn128.entity.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserStatusRepository extends JpaRepository<UserStatus, String> {

    List<UserStatus> findByUserIdAndIsConnected(String userId, Boolean isConnected);
    UserStatus findByUserIdAndUserStatusId(String userId, String userStatusId);
}
