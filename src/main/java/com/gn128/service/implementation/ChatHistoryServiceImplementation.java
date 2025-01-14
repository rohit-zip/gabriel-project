package com.gn128.service.implementation;

/*
  Developer: Rohit Parihar
  Project: gabriel-project
  GitHub: github.com/rohit-zip
  File: ChatHistoryServiceImplementation
 */

import com.gn128.dao.repository.ChatHistoryRepository;
import com.gn128.entity.ChatHistory;
import com.gn128.payloads.response.ListResponse;
import com.gn128.service.ChatHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class ChatHistoryServiceImplementation implements ChatHistoryService {

    private final ChatHistoryRepository chatHistoryRepository;

    @Override
    public CompletableFuture<ListResponse> userChatHistory(String userId, Integer page, Integer size) {
        Sort sort = Sort.by("updatedOn").ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ChatHistory> pageData = chatHistoryRepository.findAllByUserId(userId, pageable);
        List<ChatHistory> chatHistoryList = pageData.getContent();
        return CompletableFuture.completedFuture(
                ListResponse
                        .builder()
                        .object(chatHistoryList)
                        .page(page)
                        .size(size)
                        .totalElements(pageData.getTotalElements())
                        .build()
        );
    }
}
