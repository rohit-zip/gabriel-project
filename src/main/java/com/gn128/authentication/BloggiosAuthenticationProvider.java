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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.ObjectUtils;

import java.util.Objects;

/**
 * Author - rohit
 * Project - java-backend
 * Package - com.gn128.authentication
 * Created_on - November 10 - 2024
 * Created_at - 12:27
 */

public class BloggiosAuthenticationProvider extends DaoAuthenticationProvider {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        if (Objects.isNull(userDetails) || ObjectUtils.isEmpty(userDetails)) {
            throw new com.gn128.exception.payloads.AuthenticationException("User not found with given Email");
        }
        validateForInactiveUser(userDetails);
        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new com.gn128.exception.payloads.AuthenticationException("Incorrect Password");
        }
        return new AuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(AuthenticationToken.class);
    }

    private static void validateForInactiveUser(UserDetails userDetails) {
        if (!userDetails.isEnabled()) {
            throw new com.gn128.exception.payloads.AuthenticationException("User Inactive");
        }
        if (!userDetails.isAccountNonExpired()) {
            throw new com.gn128.exception.payloads.AuthenticationException("Account Expired");
        }
        if (!userDetails.isCredentialsNonExpired()) {
            throw new com.gn128.exception.payloads.AuthenticationException("Credentials Expired");
        }
        if (!userDetails.isAccountNonLocked()) {
            throw new com.gn128.exception.payloads.AuthenticationException("Account Locked");
        }
    }
}
