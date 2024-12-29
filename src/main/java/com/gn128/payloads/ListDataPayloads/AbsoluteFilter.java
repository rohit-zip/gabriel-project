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

package com.gn128.payloads.ListDataPayloads;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.gn128.enums.AbsoluteFilterKeyType;
import com.gn128.enums.Operator;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AbsoluteFilter {

    private String filterKey;
    private AbsoluteFilterKeyType filterKeyType;
    private AbsoluteFilterSelection selection;
    private Operator operator;
}
