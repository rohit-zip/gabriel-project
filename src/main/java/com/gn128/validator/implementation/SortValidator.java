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

import com.gn128.enums.Order;
import com.gn128.exception.payloads.BadRequestException;
import com.gn128.payloads.ListDataPayloads.Sort;
import com.gn128.payloads.ListPayload;
import com.gn128.utils.ValueCheckerUtil;
import com.gn128.validator.BusinessValidator;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
public class SortValidator implements BusinessValidator<ListPayload> {

    @Override
    public void doValidate(ListPayload listRequest) {
        if (!listRequest.getSorts().isEmpty()) {
            listRequest
                    .getSorts()
                    .forEach(this::initValidation);
        }
    }

    private void initValidation(Sort sort) {
        if (!ValueCheckerUtil.isDataPresent(sort.getKey()))
            throw new BadRequestException("Sort Key is not present on Sort", HttpStatus.BAD_REQUEST);
        if (ObjectUtils.isEmpty(sort.getOrder()))
            throw new BadRequestException("Sort Order is Mandatory (ASC or DESC) in Sort", HttpStatus.BAD_REQUEST);
        EnumValidator<Order> enumValidator = new EnumValidator<>(Order.class);
        if (!enumValidator.isValidEnum(sort.getOrder().name()))
            throw new BadRequestException("Sort key is not valid", HttpStatus.BAD_REQUEST);
    }
}
