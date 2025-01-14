package com.gn128.service;

/*
  Developer: Rohit Parihar
  Project: gabriel-project
  GitHub: github.com/rohit-zip
  File: ChatHistoryService
 */

import com.gn128.payloads.response.ListResponse;

import java.util.concurrent.CompletableFuture;

public interface ChatHistoryService {

    CompletableFuture<ListResponse> userChatHistory(String userId, Integer page, Integer size);
}
