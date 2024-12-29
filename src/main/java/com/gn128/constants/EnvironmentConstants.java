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

package com.gn128.constants;

import lombok.experimental.UtilityClass;

/**
 * Owner - Rohit Parihar
 * Author - rohit
 * Project - auth-provider-application
 * Package - com.bloggios.auth.provider.constants
 * Created_on - 29 November-2023
 * Created_at - 00 : 58
 */

@UtilityClass
public class EnvironmentConstants {

    public static final String APPLICATION_VERSION = "application.version";
    public static final String REFRESH_TOKEN_COOKIE_NAME = "refresh-mgmt.cookie-name";
    public static final String ACTIVE_PROFILE = "application.profile";
    public static final String GOOGLE_MAIL_HOST = "mail-configuration.google-mail.host";
    public static final String GOOGLE_MAIL_PORT = "mail-configuration.google-mail.port";
    public static final String GOOGLE_MAIL_PASSWORD = "mail-configuration.google-mail.password";
    public static final String GOOGLE_MAIL_USERNAME = "mail-configuration.google-mail.username";
    public static final String FLUTTER_WAVE_TOKEN = "flutter-wave.token";
    public static final String PROFILE_IMAGES_PATH = "files.profile";
    public static final String BLOB_STORAGE_BASE_URL = "application.assets";
}
