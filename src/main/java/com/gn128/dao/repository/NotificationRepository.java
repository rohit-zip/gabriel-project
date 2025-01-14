package com.gn128.dao.repository;

/*
  Developer: Rohit Parihar
  Project: gabriel-project
  GitHub: github.com/rohit-zip
  File: NotificationRepository
 */

import com.gn128.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, String> {
    Page<Notification> findAllByReceiverId(String receiverId, Pageable pageable);
}
