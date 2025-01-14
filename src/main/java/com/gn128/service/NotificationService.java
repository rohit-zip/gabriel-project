package com.gn128.service;

/*
  Developer: Rohit Parihar
  Project: gabriel-project
  GitHub: github.com/rohit-zip
  File: NotificationService
 */

import com.gn128.authentication.UserPrincipal;
import com.gn128.payloads.response.ListResponse;

import java.util.concurrent.CompletableFuture;

public interface NotificationService {

    void sendGlobalNotification(String message, UserPrincipal userPrincipal);
    CompletableFuture<ListResponse> notificationList(Integer page, UserPrincipal userPrincipal);
}
