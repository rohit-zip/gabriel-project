package com.gn128.service.implementation;

/*
  Developer: Rohit Parihar
  Project: gabriel-project
  GitHub: github.com/rohit-zip
  File: NotificationServiceImplementation
 */

import com.gn128.authentication.UserPrincipal;
import com.gn128.dao.repository.NotificationRepository;
import com.gn128.entity.Notification;
import com.gn128.payloads.response.ListResponse;
import com.gn128.payloads.response.ModuleResponse;
import com.gn128.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class NotificationServiceImplementation implements NotificationService {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final NotificationRepository notificationRepository;

    @Override
    public void sendGlobalNotification(String message, UserPrincipal userPrincipal) {
        ModuleResponse moduleResponse = ModuleResponse
                .builder()
                .message(message)
                .userId(userPrincipal.getUserId())
                .build();
        simpMessagingTemplate.convertAndSend("/notification/global-notification", moduleResponse);
    }

    @Override
    public CompletableFuture<ListResponse> notificationList(Integer page, UserPrincipal userPrincipal) {
        Sort sort = Sort.by("dateCreated").ascending();
        Pageable pageable = PageRequest.of(page, 10, sort);
        Page<Notification> pageData = notificationRepository.findAllByReceiverId(userPrincipal.getUserId(), pageable);
        List<Notification> notificationList = pageData.getContent();
        return CompletableFuture.completedFuture(
                ListResponse
                        .builder()
                        .object(notificationList)
                        .page(page)
                        .size(10)
                        .totalElements(pageData.getTotalElements())
                        .build()
        );
    }
}
