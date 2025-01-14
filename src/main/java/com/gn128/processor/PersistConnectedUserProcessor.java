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

import com.gn128.constants.ExceptionMessages;
import com.gn128.dao.repository.ConnectedUserRepository;
import com.gn128.dao.repository.UserAuthRepository;
import com.gn128.entity.ConnectedUser;
import com.gn128.entity.UserAuth;
import com.gn128.entity.UserStatus;
import com.gn128.exception.payloads.BadRequestException;
import com.gn128.utils.AsyncUtils;
import com.gn128.websocket.CheckAndDisconnectUserExecutor;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Owner - Rohit Parihar
 * Author - rohit
 * Project - bloggios-websockets-provider
 * Package - com.bloggios.websockets.provider.processor.implementation.voidprocess
 * Created_on - 01 March-2024
 * Created_at - 17 : 52
 */

@Component
@RequiredArgsConstructor
public class PersistConnectedUserProcessor {

    private static final Logger logger = LoggerFactory.getLogger(PersistConnectedUserProcessor.class);

    private final UserAuthRepository userAuthRepository;
    private final ConnectedUserRepository connectedUserRepository;
    private final CheckAndDisconnectUserExecutor checkAndDisconnectUserExecutor;
    private final CreateUserStatusProcessor createUserStatusProcessor;
    private final ConnectedUserCreateProcessor connectedUserCreateProcessor;

    public void process(String sessionId, String userId, String remoteAddress) {
        CompletableFuture<UserAuth> userDocumentFuture = CompletableFuture.supplyAsync(() -> userAuthRepository.findById(userId)
                .orElseThrow(()-> new BadRequestException(ExceptionMessages.USER_NOT_FOUND_ID)));
        CompletableFuture<Optional<ConnectedUser>> optionalConnectedUserFuture = CompletableFuture.supplyAsync(() -> connectedUserRepository.findById(userId));
        AsyncUtils.getAsyncResult(CompletableFuture.allOf(userDocumentFuture, optionalConnectedUserFuture));
        Optional<ConnectedUser> optionalConnectedUser = optionalConnectedUserFuture.join();
        CompletableFuture.runAsync(()-> checkAndDisconnectUserExecutor.process(userId));
        UserStatus userStatus = createUserStatusProcessor.apply(userId, sessionId, remoteAddress);
        CompletableFuture.runAsync(()-> {
            optionalConnectedUser.ifPresent(connectedUserRepository::delete);
            connectedUserCreateProcessor.process(userStatus);
        });
        logger.warn("""
                User Connected
                User Id : {}
                Session Id : {}
                User Status Id : {}
                Date : {}
                Time : {}
                """, userId, userStatus.getSessionId(), userStatus.getUserStatusId(), LocalDate.now(), LocalTime.now());
    }
}
