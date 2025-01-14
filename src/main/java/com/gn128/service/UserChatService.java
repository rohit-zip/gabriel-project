package com.gn128.service;

/*
  Developer: Rohit Parihar
  Project: gabriel-project
  GitHub: github.com/rohit-zip
  File: UserChatService
 */

import com.gn128.payloads.response.ListResponse;

import java.util.concurrent.CompletableFuture;

public interface UserChatService {

    CompletableFuture<ListResponse> userChatList(String receiverId, String senderId, Integer page, Integer size);
}
