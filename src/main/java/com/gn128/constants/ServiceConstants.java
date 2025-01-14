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
 * Created_at - 23 : 46
 */

@UtilityClass
public class ServiceConstants {

    public static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";
    public static final String PASSWORD_REGEX = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{9,}$";
    public static final String RANDOM_UUID = "randomUUID";
    public static final String ALPHABETS_REGEX = "^[a-zA-Z ]+$";
    public static final Integer MINUTES_7 = 1000 * 60 * 7;
    public static final String AUTHORIZATION = "Authorization";
    public static final String AUTHORITIES = "authority";
    public static final String ISSUER = "https://bloggios.com";
    public static final String AUTHORITY = "authority";
    public static final String USER_EMAIL = "email";
    public static final String ORIGIN = "Origin";
    public static final String LOCAL_ORIGIN = "http://localhost:3000";
    public static final Integer ZERO = 0;
    public static final Integer TEN = 10;
    public static final int INNER_HITS_SIZE=500;
    public static final String DEFAULT_NORMALIZER = "default_normalizer_keyword";
    public static final String DEFAULT_AUTOCOMPLETE = "default_autocomplete_text";
    public static final String DATE_REGISTERED = "dateRegistered";
    public static final String USER_TABLE = "user_entity";
    public static final String ROLE_ENTITY_TABLE_NAME = "role";
    public static final String SEARCH_PREFIX_ES_QUERY = "search_";
    public static final String FILTER_PREFIX_ES_QUERY = "filter_";
    public static final String USER_IP = "userIp";
    public static final String X_FORWARDED_FOR = "X-Forwarded-For";
    public static final String DEFAULT_IP = "1.1.1.1";
    public static final String DUMMY_ROLE = "dummy";
    public static final String BLOGGIOS_COM = "bloggios.com";
    public static final String BLOGGIOS_IN = "bloggios.in";
    public static final String BLOGGIOS_CLOUD = "bloggios.cloud";
    public static final String VERBATIM = "verbatim";
    public static final String LISTENER_TOPIC = "#{__listener.topics}";
    public static final String LISTENER_GROUP_ID = "#{__listener.groupId}";
    public static final String CONTAINER_FACTORY_BEAN_NAME = "#{__listener.containerFactoryBeanName}";
    public static final String TOPICS = "Topics";
    public static final String SERVICE_NAME = "ServiceName";
    public static final String SERVICE_CONTAINER_FACTORY_NAME = "ServiceContainerFactoryName";
    public static final String ERROR_MESSAGES_FILE = "classpath:error-messages.properties";
    public static final String ADMIN_ROLE = "admin";
    public static final String USER_ROLE = "user";
    public static final String USERNAME = "username";
    public static final String ENVIRONMENT = "environment";
    public static final String TOKEN_TYPE = "type";
    public static final String EXTENDED_TOKEN = "long";
    public static final String NORMAL_TOKEN = "normal";
    public static final String REMOTE_ADDRESS = "remoteAddress";
    public static final String UUID_STRATEGY = "org.hibernate.id.UUIDGenerator";
    public static final String BREADCRUMB_ID = "breadcrumbId";
    public static final String INTERNAL_ERROR_TYPE = "INTERNAL ERROR";
    public static final String ACCESS_DENIED_ERROR_CODE = "AE__AUTH-ACCESS-DENIED";
    public static final String DATA_ERROR_TYPE = "DATA ERROR";
    public static final String COOKIE_TOKEN = "cookie-token";
    public static final String IS_BADGE = "is-badge";
    public static final String INTERNAL_ERROR_MESSAGE = "Something went wrong at server side. Please try again later or contact support";
    public static final String GN128_COM = "gn128.com";
    public static final String PROFILE_PGSQL_BUILDER_COMPONENT = "profilePgsqlBuilderImplementation";
    public static final String STRING = "STRING";
    public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final String DESCENDING = "DESC";
    public static final String DATE_CREATED = "dateCreated";
    public static final String UPLOAD_PROFILE = "profile";
    public static final String USER_ID = "userId";
    public static final String ACCESS_TOKEN = "accessToken";
}
