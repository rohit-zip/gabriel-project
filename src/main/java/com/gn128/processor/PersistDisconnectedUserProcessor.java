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

import com.gn128.dao.repository.ConnectedUserRepository;
import com.gn128.dao.repository.UserStatusRepository;
import com.gn128.entity.ConnectedUser;
import com.gn128.entity.UserStatus;
import com.gn128.utils.AsyncUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Owner - Rohit Parihar
 * Author - rohit
 * Project - bloggios-websockets-provider
 * Package - com.bloggios.websockets.provider.processor.implementation.voidprocess
 * Created_on - 01 March-2024
 * Created_at - 19 : 24
 */

@Component
@RequiredArgsConstructor
public class PersistDisconnectedUserProcessor {

    private static final Logger logger = LoggerFactory.getLogger(PersistDisconnectedUserProcessor.class);

    private final ConnectedUserRepository connectedUserRepository;
    private final UserStatusRepository userStatusRepository;

    public void process(String sessionId) {
        Optional<ConnectedUser> optionalConnectedUser = connectedUserRepository.findBySessionId(sessionId);
        if (optionalConnectedUser.isEmpty()) {
            logger.warn("""
                    Important
                    Flow Revision Required for Disconnection Interception
                    Disconnection Interceptor raised for Session Id {}, but Connected user is not available in the Database
                    No action taken from Server side
                    """, sessionId);
        } else {
            ConnectedUser connectedUser = optionalConnectedUser.get();
            CompletableFuture<UserStatus> userStatusCompletableFuture = CompletableFuture.supplyAsync(() -> userStatusRepository.findByUserIdAndUserStatusId(
                    connectedUser.getUserId(),
                    connectedUser.getUserStatusId()
            ));
            CompletableFuture<Void> voidCompletableFuture = CompletableFuture.runAsync(() -> connectedUserRepository.delete(connectedUser));
            AsyncUtils.getAsyncResult(CompletableFuture.allOf(userStatusCompletableFuture, voidCompletableFuture));
            UserStatus userStatus = userStatusCompletableFuture.join();
            userStatus.setIsConnected(Boolean.FALSE);
            userStatus.setDisconnectedOn(Date.from(Instant.now()));
            userStatus.setDisconnectedAnonymously(Boolean.FALSE);
            CompletableFuture.runAsync(()-> userStatusRepository.save(userStatus));
            logger.warn("""
                User Connected
                User Id : {}
                Session Id : {}
                User Status Id : {}
                Date : {}
                Time : {}
                """, connectedUser.getUserId(), userStatus.getSessionId(), userStatus.getUserStatusId(), LocalDate.now(), LocalTime.now());
        }
    }
}
