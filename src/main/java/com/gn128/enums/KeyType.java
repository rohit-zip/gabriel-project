/*
 * Copyright 2023 Rohit Parihar and Bloggios
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

package com.gn128.enums;

import java.util.Arrays;
import java.util.Optional;

public enum KeyType {
    INTEGER,
    STRING,
    BOOLEAN,
    LONG,
    ENUM,
    DOUBLE,
    DATE_TIME;

    public static KeyType getInstance(String value){
        Optional<KeyType> enumInstance = Arrays.stream(KeyType.values())
                .filter(a -> value.equals(a.name()))
                .findFirst();
        return enumInstance.orElse(null);
    }
}
