package com.gn128.dao.repository;

/*
  Developer: Rohit Parihar
  Project: gabriel-project
  GitHub: github.com/rohit-zip
  File: ChatHistoryRepository
 */

import com.gn128.entity.ChatHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatHistoryRepository extends JpaRepository<ChatHistory, String> {

    Optional<ChatHistory> findByUserIdAndReceiverId(String userId, String receiverId);
    Page<ChatHistory> findAllByUserId(String userId, Pageable pageable);
}
