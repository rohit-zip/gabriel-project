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

package com.gn128.validator.implementation;

import com.gn128.exception.payloads.BadRequestException;
import com.gn128.payloads.ListDataPayloads.Filter;
import com.gn128.payloads.ListPayload;
import com.gn128.validator.BusinessValidator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
public class FilterValidator implements BusinessValidator<ListPayload> {

    @Override
    public void doValidate(ListPayload listRequest) {
        if (!ObjectUtils.isEmpty(listRequest.getFilters()))
            listRequest.getFilters().forEach(this::initValidation);
    }

    private void initValidation(Filter filter) {
        if (StringUtils.isBlank(filter.getFilterKey()))
            throw new BadRequestException("Filter key is Mandatory in Filter", HttpStatus.BAD_REQUEST);
        else if (ObjectUtils.isEmpty(filter.getSelections()))
            throw new BadRequestException("Filter Selections is Mandatory in Filter", HttpStatus.BAD_REQUEST);
    }
}
