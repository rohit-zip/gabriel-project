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

package com.gn128.utils;

import com.gn128.entity.ForgetPassword;
import com.gn128.entity.RegistrationOtp;
import com.gn128.entity.UserAuth;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Date;
import java.util.UUID;
import java.util.function.Supplier;

import static com.gn128.constants.ServiceConstants.MINUTES_7;

/**
 * Owner - Rohit Parihar
 * Author - rohit
 * Project - auth-provider-application
 * Package - com.bloggios.auth.provider.utils
 * Created_on - 02 December-2023
 * Created_at - 23 : 56
 */

@Component
public class OtpGenerator {

    private final Supplier<String> generateOtp = () -> {
        StringBuilder string = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();
        for (int i=0 ; i<6 ; i++){
            string.append(secureRandom.nextInt(9));
        }
        return string.toString();
    };

    public RegistrationOtp registrationOtpSupplier(UserAuth userAuth){
        return RegistrationOtp
                .builder()
                .otpId(UUID.randomUUID().toString())
                .userId(userAuth.getUserId())
                .email(userAuth.getEmail())
                .otp(generateOtp.get())
                .dateGenerated(new Date())
                .expiry(new Date(System.currentTimeMillis() + MINUTES_7))
                .build();
    }

    public ForgetPassword forgetPasswordOtpSupplier(UserAuth userAuth) {
        return ForgetPassword
                .builder()
                .otp(generateOtp.get())
                .dateGenerated(new Date())
                .expiry(new Date(System.currentTimeMillis() + MINUTES_7))
                .userId(userAuth.getUserId())
                .email(userAuth.getEmail())
                .build();
    }
}
