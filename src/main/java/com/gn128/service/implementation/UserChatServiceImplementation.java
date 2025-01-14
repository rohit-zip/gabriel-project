package com.gn128.service.implementation;

/*
  Developer: Rohit Parihar
  Project: gabriel-project
  GitHub: github.com/rohit-zip
  File: UserChatServiceImplementation
 */

import com.gn128.dao.repository.ChatRepository;
import com.gn128.entity.Chat;
import com.gn128.payloads.response.ListResponse;
import com.gn128.service.UserChatService;
import com.gn128.utils.ValueCheckerUtil;
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
public class UserChatServiceImplementation implements UserChatService {

    private final ChatRepository chatRepository;

    @Override
    public CompletableFuture<ListResponse> userChatList(String receiverId, String senderId, Integer page, Integer size) {
        ValueCheckerUtil.isValidUUID(senderId);
        ValueCheckerUtil.isValidUUID(receiverId);
        Sort sort = Sort.by("dateCreated").descending();
        Pageable pageable = PageRequest.of(page, 10, sort);
        Page<Chat> pageData = chatRepository.findAllByChatKey(senderId + "_" + receiverId, pageable);
        List<Chat> chatList = pageData.getContent();
        return CompletableFuture.completedFuture(
                ListResponse
                        .builder()
                        .object(chatList)
                        .page(page)
                        .size(10)
                        .totalElements(pageData.getTotalElements())
                        .build()
        );
    }
}
