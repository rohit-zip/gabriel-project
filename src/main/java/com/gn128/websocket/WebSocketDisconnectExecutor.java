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

package com.gn128.websocket;

import com.bloggios.websockets.provider.constants.InternalErrorExceptionCodes;
import com.bloggios.websockets.provider.processor.VoidProcess;
import com.bloggios.websockets.provider.processor.implementation.voidprocess.PersistDisconnectedUserProcessor;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * Owner - Rohit Parihar
 * Author - rohit
 * Project - bloggios-websockets-provider
 * Package - com.bloggios.websockets.provider.processor.executor
 * Created_on - 01 March-2024
 * Created_at - 13 : 29
 */

@Component
public class WebSocketDisconnectExecutor implements VoidProcess<StompHeaderAccessor> {

    private final PersistDisconnectedUserProcessor persistDisconnectedUserProcessor;

    public WebSocketDisconnectExecutor(
            PersistDisconnectedUserProcessor persistDisconnectedUserProcessor
    ) {
        this.persistDisconnectedUserProcessor = persistDisconnectedUserProcessor;
    }

    @Override
    public void process(
            @NotNull(message = InternalErrorExceptionCodes.STOMP_HEADER_ACCESSOR_NULL) StompHeaderAccessor stompHeaderAccessor
    ) {
        String sessionId = stompHeaderAccessor.getSessionId();
        if (Objects.nonNull(sessionId)) {
            persistDisconnectedUserProcessor.process(sessionId);
        }
    }
}
