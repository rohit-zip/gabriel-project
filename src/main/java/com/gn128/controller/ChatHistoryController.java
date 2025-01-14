package com.gn128.controller;

/*
  Developer: Rohit Parihar
  Project: gabriel-project
  GitHub: github.com/rohit-zip
  File: ChatHistoryController
 */

import com.gn128.payloads.response.ListResponse;
import com.gn128.service.ChatHistoryService;
import com.gn128.utils.AsyncUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chat-history")
@RequiredArgsConstructor
public class ChatHistoryController {

    private final ChatHistoryService chatHistoryService;

    @GetMapping("/list")
    public ResponseEntity<ListResponse> userChatHistory(
            @RequestParam String userId,
            @RequestParam Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size
    ) {
        return ResponseEntity.ok(AsyncUtils.getAsyncResult(chatHistoryService.userChatHistory(
                userId,
                page,
                size
        )));
    }
}
