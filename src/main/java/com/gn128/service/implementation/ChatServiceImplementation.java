package com.gn128.service.implementation;

/*
  Developer: Rohit Parihar
  Project: gabriel-project
  GitHub: github.com/rohit-zip
  File: ChatServiceImplementation
 */

import com.gn128.constants.BeanNameConstants;
import com.gn128.dao.repository.ChatRepository;
import com.gn128.dao.repository.ConnectedUserRepository;
import com.gn128.dao.repository.UserAuthRepository;
import com.gn128.entity.Chat;
import com.gn128.entity.ConnectedUser;
import com.gn128.entity.UserAuth;
import com.gn128.enums.ChatStatus;
import com.gn128.enums.ChatType;
import com.gn128.exception.payloads.BadRequestException;
import com.gn128.payloads.record.MessageRecord;
import com.gn128.payloads.response.ModuleResponse;
import com.gn128.processor.ChatHistoryCreateOrUpdateProcessor;
import com.gn128.service.ChatService;
import com.gn128.utils.AsyncUtils;
import com.gn128.websocket.MessageExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatServiceImplementation implements ChatService {

    private final UserAuthRepository userAuthRepository;
    private final ConnectedUserRepository connectedUserRepository;
    private final ChatRepository chatRepository;
    private final ChatHistoryCreateOrUpdateProcessor chatHistoryCreateOrUpdateProcessor;
    private final MessageExecutor messageExecutor;

    @Override
    @Async(BeanNameConstants.ASYNC_TASK_EXTERNAL_POOL)
    public CompletableFuture<ModuleResponse> sendPrivateMessage(MultipartFile file, String senderId, String receiverId, String message) {
        CompletableFuture<UserAuth> receiverIdFuture = CompletableFuture.supplyAsync(() -> userAuthRepository.findById(receiverId)
                .orElseThrow(() -> new BadRequestException("Receiver Not found for the particular user")));
        CompletableFuture<UserAuth> senderIdFuture = CompletableFuture.supplyAsync(() -> userAuthRepository.findById(senderId)
                .orElseThrow(() -> new BadRequestException("Receiver Not found for the particular user")));
        CompletableFuture<Optional<ConnectedUser>> optionalConnectedUser = CompletableFuture.supplyAsync(() -> connectedUserRepository.findById(receiverId));
        AsyncUtils.getAsyncResult(CompletableFuture.allOf(receiverIdFuture, senderIdFuture, optionalConnectedUser));
        Optional<ConnectedUser> connectedUserOptional = optionalConnectedUser.join();
        UserAuth receiverUser = receiverIdFuture.join();
        UserAuth senderUser = senderIdFuture.join();
        ChatStatus sentStatus = ChatStatus.SENT;
        if (connectedUserOptional.isPresent()) {
            sentStatus = ChatStatus.DELIVERED;
        }
        String senderChatId = UUID.randomUUID().toString();
        String receiverChatId = UUID.randomUUID().toString();
        Chat senderChatDocument = Chat
                .builder()
                .chatId(senderChatId + "_" + receiverChatId)
                .chatKey(senderUser.getUserId() + "_" + receiverUser.getUserId())
                .receiverId(receiverUser.getUserId())
                .senderId(senderUser.getUserId())
                .message(message)
                .dateCreated(Date.from(Instant.now()))
                .chatStatus(sentStatus)
                .chatType(ChatType.SENDER)
                .deliveredOn(sentStatus.equals(ChatStatus.DELIVERED) ? Date.from(Instant.now()) : null)
                .build();
        Chat receiverChatDocument = Chat
                .builder()
                .chatId(receiverChatId + "_" + senderChatId)
                .chatKey(receiverUser.getUserId() + "_" + senderUser.getUserId())
                .receiverId(receiverUser.getUserId())
                .senderId(senderUser.getUserId())
                .message(message)
                .dateCreated(Date.from(Instant.now()))
                .chatStatus(sentStatus)
                .chatType(ChatType.RECEIVER)
                .deliveredOn(sentStatus.equals(ChatStatus.DELIVERED) ? Date.from(Instant.now()) : null)
                .build();
        CompletableFuture.runAsync(() -> {
            Chat response = chatRepository.save(senderChatDocument);
            chatRepository.save(receiverChatDocument);
            chatHistoryCreateOrUpdateProcessor.process(response);
        }).exceptionally(throwable -> {
            log.error(throwable.getMessage());
            return null;
        });
        messageExecutor.sendPrivateMessage(new MessageRecord(
                senderChatDocument.getSenderId(),
                senderChatDocument.getReceiverId(),
                senderChatDocument.getMessage(),
                senderChatDocument.getDateCreated()
        ));
        log.debug("Message sent successfully to {}", senderChatDocument.getReceiverId());
        return null;
    }
}
