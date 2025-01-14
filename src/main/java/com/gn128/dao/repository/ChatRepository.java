package com.gn128.dao.repository;

/*
  Developer: Rohit Parihar
  Project: gabriel-project
  GitHub: github.com/rohit-zip
  File: ChatRepository
 */

import com.gn128.entity.Chat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRepository extends JpaRepository<Chat, String> {
    Page<Chat> findAllByChatKey(String chatKey, Pageable pageable);
}
