package com.gn128.dao.repository;

/*
  Developer: Rohit Parihar
  Project: gabriel-project
  GitHub: github.com/rohit-zip
  File: ConnectedUserRepository
 */

import com.gn128.entity.ConnectedUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConnectedUserRepository extends JpaRepository<ConnectedUser, String> {

    Optional<ConnectedUser> findBySessionId(String sessionId);
}
