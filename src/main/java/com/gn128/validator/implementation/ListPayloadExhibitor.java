package com.gn128.validator.implementation;

import com.gn128.payloads.ListPayload;
import com.gn128.validator.Exhibitor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Author - rohit
 * Project - java-backend
 * Package - com.gn128.validator.implementation
 * Created_on - December 04 - 2024
 * Created_at - 23:44
 */

@Component
@RequiredArgsConstructor
public class ListPayloadExhibitor implements Exhibitor<ListPayload> {

    private final ListPayloadFieldsValidationProvider listPayloadFieldsValidationProvider;

    @Override
    public void validateAll(ListPayload listPayload) {
        listPayloadFieldsValidationProvider
                .provide()
                .forEach(listRequestBusinessValidator -> listRequestBusinessValidator.validator(listPayload));
    }
}
