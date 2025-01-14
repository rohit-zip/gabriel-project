/*
 * Copyright © 2023-2024 Rohit Parihar and Bloggios
 * All rights reserved.
 * This software is the property of Rohit Parihar and is protected by copyright law.
 * The software, including its source code, documentation, and associated files, may not be used, copied, modified, distributed, or sublicensed without the express written consent of Rohit Parihar.
 * For licensing and usage inquiries, please contact Rohit Parihar at rohitparih@gmail.com, or you can also contact support@bloggios.com.
 * This software is provided as-is, and no warranties or guarantees are made regarding its fitness for any particular purpose or compatibility with any specific technology.
 * For license information and terms of use, please refer to the accompanying LICENSE file or visit http://www.apache.org/licenses/LICENSE-2.0.
 * Unauthorized use of this software may result in legal action and liability for damages.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gn128.processor;

import com.gn128.dao.repository.ChatHistoryRepository;
import com.gn128.entity.Chat;
import com.gn128.entity.ChatHistory;
import com.gn128.transformer.ChatDocumentToChatHistoryTransformer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Owner - Rohit Parihar
 * Author - rohit
 * Project - bloggios-websockets-provider
 * Package - com.bloggios.websockets.provider.processor.implementation.voidprocess
 * Created_on - 28 March-2024
 * Created_at - 16 : 18
 */

@Component
@Slf4j
@RequiredArgsConstructor
public class ChatHistoryCreateOrUpdateProcessor {

    private final ChatHistoryRepository chatHistoryRepository;
    private final ChatDocumentToChatHistoryTransformer chatDocumentToChatHistoryTransformer;
    
    public void process(Chat chat) {

        CompletableFuture<Optional<ChatHistory>> optionalPrimaryChatHistory = CompletableFuture.supplyAsync(() -> chatHistoryRepository.findByUserIdAndReceiverId(chat.getSenderId(), chat.getReceiverId()));
        CompletableFuture<Optional<ChatHistory>> optionalSecondaryChatHistory = CompletableFuture.supplyAsync(() -> chatHistoryRepository.findByUserIdAndReceiverId(chat.getReceiverId(), chat.getSenderId()));
        CompletableFuture.allOf(optionalPrimaryChatHistory, optionalSecondaryChatHistory);
        Optional<ChatHistory> primaryChat = optionalPrimaryChatHistory.join();
        Optional<ChatHistory> secondaryChat = optionalSecondaryChatHistory.join();

        if (primaryChat.isPresent() && secondaryChat.isPresent()) {
            CompletableFuture<Void> primaryVoid = CompletableFuture.runAsync(() -> updateChatHistory(primaryChat));
            CompletableFuture<Void> secondaryVoid = CompletableFuture.runAsync(() -> updateChatHistory(secondaryChat));
            CompletableFuture.allOf(primaryVoid, secondaryVoid);
        } else if (primaryChat.isEmpty() && secondaryChat.isEmpty()) {
            ChatHistory primaryChatHistoryDocument = chatDocumentToChatHistoryTransformer.apply(chat);
            ChatHistory secondaryChatHistoryDocument = chatDocumentToChatHistoryTransformer.apply(chat.getReceiverId(), chat.getSenderId());
            CompletableFuture<Void> primaryFuture = CompletableFuture.runAsync(() -> createChatHistory(primaryChatHistoryDocument));
            CompletableFuture<Void> secondaryFuture = CompletableFuture.runAsync(() -> createChatHistory(secondaryChatHistoryDocument));
            CompletableFuture.allOf(primaryFuture, secondaryFuture);
        } else if (primaryChat.isEmpty()) {
            ChatHistory primaryChatHistoryDocument = chatDocumentToChatHistoryTransformer.apply(chat);
            CompletableFuture<Void> primaryChatCreateFuture = CompletableFuture.runAsync(() -> createChatHistory(primaryChatHistoryDocument));
            CompletableFuture<Void> secondaryChatUpdateFuture = CompletableFuture.runAsync(() -> updateChatHistory(secondaryChat));
            CompletableFuture.allOf(primaryChatCreateFuture, secondaryChatUpdateFuture);
            log.warn("""
                    Data Redundancy Observed for Chat History
                    Description : Primary Chat is not present but Secondary Chat is present
                    Sender Id : {}
                    Receiver Id : {}
                    """,
                    chat.getSenderId(),
                    chat.getReceiverId());
        } else {
            ChatHistory secondaryChatHistoryDocument = chatDocumentToChatHistoryTransformer.apply(chat.getReceiverId(), chat.getSenderId());
            CompletableFuture<Void> primaryChatUpdateFuture = CompletableFuture.runAsync(() -> updateChatHistory(primaryChat));
            CompletableFuture<Void> secondaryChatCreateFuture = CompletableFuture.runAsync(() -> createChatHistory(secondaryChatHistoryDocument));
            CompletableFuture.allOf(primaryChatUpdateFuture, secondaryChatCreateFuture);
            log.warn("""
                    Data Redundancy Observed for Chat History
                    Description : Primary Chat is present but Secondary Chat is not present
                    Sender Id : {}
                    Receiver Id : {}
                    """,
                    chat.getSenderId(),
                    chat.getReceiverId());
        }
    }

    private void updateChatHistory(Optional<ChatHistory> history) {
        if (history.isPresent()) {
            ChatHistory chatHistory = history.get();
            chatHistory.setUpdatedOn(Date.from(Instant.now()));
            chatHistoryRepository.save(chatHistory);
            log.debug("""
                                Chat History window Updated for below details
                                User Id : {}
                                Sender Id : {}
                            """,
                    chatHistory.getUserId(),
                    chatHistory.getReceiverId());
        }
    }

    private void createChatHistory(ChatHistory chatHistory) {
        ChatHistory response = chatHistoryRepository.save(chatHistory);
        log.info("""
                    Chat History window initialized for below details
                    User Id : {}
                    Sender Id : {}
                    """,
                response.getUserId(),
                response.getReceiverId());
    }
}
