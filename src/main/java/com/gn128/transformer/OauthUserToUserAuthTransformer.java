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

package com.gn128.transformer;

import com.gn128.constants.EnvironmentConstants;
import com.gn128.dao.repository.RoleRepository;
import com.gn128.entity.Role;
import com.gn128.entity.UserAuth;
import com.gn128.enums.Provider;
import com.gn128.exception.payloads.BadRequestException;
import com.gn128.payloads.GoogleOauthUserInfo;
import com.gn128.utils.IpUtils;
import com.gn128.utils.UsernameGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.gn128.constants.ServiceConstants.USER_ROLE;

/**
 * Owner - Rohit Parihar and Bloggios
 * Author - rohit
 * Project - auth-provider-application
 * Package - com.bloggios.auth.provider.transformer.implementation
 * Created_on - June 12 - 2024
 * Created_at - 23:10
 */

@Component
@RequiredArgsConstructor
public class OauthUserToUserAuthTransformer {

    private final RoleRepository roleDao;
    private final Environment environment;
    private final UsernameGenerator usernameGenerator;

    public UserAuth transform(GoogleOauthUserInfo googleOauthUserInfo, HttpServletRequest httpServletRequest) {
        Role userRole = roleDao.findById(USER_ROLE)
                .orElseThrow(()-> new BadRequestException("Role not found with Id", HttpStatus.INTERNAL_SERVER_ERROR));
        List<Role> roleEntities = new ArrayList<>(List.of(userRole));
        return UserAuth.builder()
                .oauthId(googleOauthUserInfo.getGoogleUserId())
                .username(usernameGenerator.generate(googleOauthUserInfo.getEmail()))
                .email(googleOauthUserInfo.getEmail())
                .apiVersion(environment.getProperty(EnvironmentConstants.APPLICATION_VERSION))
                .provider(Provider.google)
                .timesDisabled(0)
                .isEnabled(googleOauthUserInfo.isEmailVerified())
                .isAccountNonExpired(true)
                .isAccountNonLocked(true)
                .isCredentialsNonExpired(true)
                .dateRegistered(new Date())
                .remoteAddress(IpUtils.getRemoteAddress(httpServletRequest))
                .isProfileAdded(false)
                .roles(roleEntities)
                .build();
    }
}
