package com.gn128.validator.implementation;

import com.gn128.constants.ServiceConstants;
import com.gn128.exception.payloads.AuthenticationException;
import com.gn128.payloads.request.AuthenticationRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.regex.Pattern;

/**
 * Author - rohit
 * Project - java-backend
 * Package - com.gn128.validator
 * Created_on - November 10 - 2024
 * Created_at - 15:23
 */

@Component
public class AuthenticationRequestValidator {

    public void validate(AuthenticationRequest authenticationRequest) {
        if (ObjectUtils.isEmpty(authenticationRequest.getEntrypoint())) {
            throw new AuthenticationException("Please enter email or username");
        }
        if (authenticationRequest.getEntrypoint().contains("@")) {
            validateEmail(authenticationRequest.getEntrypoint());
        }
        validatePassword(authenticationRequest.getPassword());
    }

    private void validateEmail(String email) {
        if (!email.matches(ServiceConstants.EMAIL_REGEX)) {
            throw new AuthenticationException("Please enter correct email address");
        }
    }

    private void validatePassword(String password) {
        if (ObjectUtils.isEmpty(password)) {
            throw new AuthenticationException("Password is mandatory and could not be empty");
        }
        if (!Pattern.matches(ServiceConstants.PASSWORD_REGEX, password)) {
            throw new AuthenticationException("Password must contain 1 Uppercase, 1 Lowercase, 1 Number and 1 Special Case with more than or equal to 8 digits");
        }
    }
}
