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

package com.gn128.transformer;

import com.gn128.entity.Chat;
import com.gn128.entity.ChatHistory;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import java.util.function.Function;

/**
 * Owner - Rohit Parihar
 * Author - rohit
 * Project - bloggios-websockets-provider
 * Package - com.bloggios.websockets.provider.transformer.implementation.function
 * Created_on - 28 March-2024
 * Created_at - 16 : 56
 */

@Component
@RequiredArgsConstructor
public class ChatDocumentToChatHistoryTransformer implements Function<Chat, ChatHistory> {

    private final Environment environment;

    @Override
    public ChatHistory apply(Chat chat) {
        return ChatHistory
                .builder()
                .chatHistoryId(UUID.randomUUID().toString())
                .userId(chat.getSenderId())
                .receiverId(chat.getReceiverId())
                .createdOn(Date.from(Instant.now()))
                .updatedOn(Date.from(Instant.now()))
                .build();
    }

    public ChatHistory apply(String senderId, String receiverId) {
        return ChatHistory
                .builder()
                .chatHistoryId(UUID.randomUUID().toString())
                .userId(senderId)
                .receiverId(receiverId)
                .createdOn(Date.from(Instant.now()))
                .updatedOn(Date.from(Instant.now()))
                .build();
    }
}
