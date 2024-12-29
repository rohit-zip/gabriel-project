/*
 * Copyright © 2023-2024 Bloggios
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

package com.gn128.exception;

import com.gn128.exception.payloads.AuthenticationException;
import com.gn128.exception.payloads.BadRequestException;
import com.gn128.payloads.response.ExceptionResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Owner - Rohit Parihar
 * Author - rohit
 * Project - auth-provider-application
 * Package - com.bloggios.auth.provider.exception
 * Created_on - 30 November-2023
 * Created_at - 01 : 57
 */

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private static ExceptionResponse getUncaughtExceptionResponse(Exception exception) {
        return ExceptionResponse
                .builder()
                .message(exception.getMessage())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.name())
                .build();
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ExceptionResponse> handleBadRequestException(BadRequestException badRequestException) {
        ExceptionResponse exceptionResponse = ExceptionResponse
                .builder()
                .message(badRequestException.getMessage())
                .status(badRequestException.getStatus().toString())
                .build();
        log.error("""
                BadRequestException Occurred
                Message : {}
                Status: {}
                """,
                exceptionResponse.getMessage(),
                exceptionResponse.getStatus()
                );
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ExceptionResponse> handleAuthenticationException(AuthenticationException exception) {
        ExceptionResponse exceptionResponse = ExceptionResponse
                .builder()
                .message(exception.getMessage())
                .status(HttpStatus.UNAUTHORIZED.toString())
                .build();
        log.error("""
                Authentication Exception Occurred
                Message : {}
                Status: {}
                """,
                exceptionResponse.getMessage(),
                exceptionResponse.getStatus()
        );
        return new ResponseEntity<>(exceptionResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleException(Exception exception) {
        ExceptionResponse exceptionResponse = getUncaughtExceptionResponse(exception);
        log.error("""
                Unknown Exception Occurred
                Message : {}
                Status: {}
                """,
                exceptionResponse.getMessage(),
                exceptionResponse.getStatus()
        );
        return new ResponseEntity<>(exceptionResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ExceptionResponse> accessDeniedException(AccessDeniedException exception) {
        ExceptionResponse exceptionResponse = ExceptionResponse
                .builder()
                .message(exception.getMessage())
                .status(HttpStatus.FORBIDDEN.toString())
                .build();
        log.error("""
                Access Denied Exception Occurred
                Message : {}
                Status: {}
                """,
                exceptionResponse.getMessage(),
                exceptionResponse.getStatus()
        );
        return new ResponseEntity<>(
                exceptionResponse,
                HttpStatus.FORBIDDEN
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ExceptionResponse> handleHttpMessageNoReadableException(HttpMessageNotReadableException exception) {
        ExceptionResponse exceptionResponse = ExceptionResponse
                .builder()
                .message(exception.getMessage())
                .status(HttpStatus.NOT_ACCEPTABLE.toString())
                .build();
        log.error("""
                HTTP Message Not Readable Exception Occurred
                Message : {}
                Status: {}
                """,
                exceptionResponse.getMessage(),
                exceptionResponse.getStatus()
        );
        return ResponseEntity
                .badRequest()
                .body(exceptionResponse);
    }
}
