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

package com.gn128.authentication;

import com.gn128.dao.repository.UserAuthRepository;
import com.gn128.entity.UserAuth;
import com.gn128.exception.payloads.AuthenticationException;
import com.gn128.exception.payloads.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Author - rohit
 * Project - java-backend
 * Package - com.gn128.authentication
 * Created_on - November 10 - 2024
 * Created_at - 12:27
 */

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final UserAuthRepository userAuthRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserAuth> byEmail = userAuthRepository.findByEmail(username);
        if (byEmail.isEmpty()) throw new AuthenticationException(
                "User not found by given Email"
        );
        return UserPrincipal.create(byEmail.get());
    }

    @Transactional
    public UserDetails loadUserById(String id) {
        UserAuth userAuth = userAuthRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("User not found by given Id", HttpStatus.NOT_FOUND));
        return UserPrincipal.create(userAuth);
    }
}
