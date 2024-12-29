package com.gn128.validator.implementation;

import com.gn128.payloads.ListPayload;
import com.gn128.validator.BusinessValidator;
import com.gn128.validator.FieldValidationProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author - rohit
 * Project - java-backend
 * Package - com.gn128.validator.implementation
 * Created_on - December 04 - 2024
 * Created_at - 23:46
 */

@Component
@RequiredArgsConstructor
public class ListPayloadFieldsValidationProvider implements FieldValidationProvider<ListPayload> {

    private final AbsoluteFilterValidator absoluteFilterValidator;
    private final FilterValidator filterValidator;
    private final SearchValidator searchValidator;
    private final SortValidator sortValidator;
    private Map<String, List<BusinessValidator<ListPayload>>> commonValidators;

    @PostConstruct
    void setup(){
        commonValidators = new HashMap<>();
        commonValidators.put("list", initiateCreateValidation());
    }

    private List<BusinessValidator<ListPayload>> initiateCreateValidation(){
        return List.of(
                absoluteFilterValidator,
                filterValidator,
                searchValidator,
                sortValidator
        );
    }

    @Override
    public List<BusinessValidator<ListPayload>> provide() {
        return commonValidators.get("list");
    }
}
