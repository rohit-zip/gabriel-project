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

import com.gn128.constants.ServiceConstants;
import com.gn128.exception.payloads.AuthenticationException;
import com.gn128.processor.PersistConnectedUserProcessor;
import com.gn128.utils.WebSocketTokenValidation;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Owner - Rohit Parihar
 * Author - rohit
 * Project - bloggios-websockets-provider
 * Package - com.bloggios.websockets.provider.processor.executor
 * Created_on - 01 March-2024
 * Created_at - 13 : 28
 */

@Component
@RequiredArgsConstructor
public class WebSocketConnectExecutor {

    private final WebSocketTokenValidation webSocketTokenValidation;
    private final PersistConnectedUserProcessor persistConnectedUserProcessor;

    public void process(StompHeaderAccessor stompHeaderAccessor) {
        String userId = stompHeaderAccessor.getFirstNativeHeader(ServiceConstants.USER_ID);
        String accessToken = stompHeaderAccessor.getFirstNativeHeader(ServiceConstants.ACCESS_TOKEN);
        String remoteAddress = stompHeaderAccessor.getFirstNativeHeader(ServiceConstants.REMOTE_ADDRESS);
        if (Objects.isNull(userId)) throw new AuthenticationException("User Id not present for websocket authentication");
        if (Objects.isNull(accessToken)) throw new AuthenticationException("Access token not present for websocket authentication");
        webSocketTokenValidation.validate(accessToken, userId);
        String sessionId = stompHeaderAccessor.getSessionId();
        remoteAddress = Objects.nonNull(remoteAddress) ? remoteAddress : "defaultRemote";
        persistConnectedUserProcessor.process(sessionId, userId, remoteAddress);
    }
}
