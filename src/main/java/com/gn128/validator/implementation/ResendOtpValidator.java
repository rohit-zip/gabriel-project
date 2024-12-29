package com.gn128.validator.implementation;

import com.gn128.constants.ExceptionMessages;
import com.gn128.entity.RegistrationOtp;
import com.gn128.entity.UserAuth;
import com.gn128.enums.Provider;
import com.gn128.exception.payloads.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * Author - rohit
 * Project - java-backend
 * Package - com.gn128.validator
 * Created_on - November 10 - 2024
 * Created_at - 21:03
 */

@Component
public class ResendOtpValidator {

    public void validate(RegistrationOtp registrationOtp, UserAuth userAuth) {
        timesSentValidator(registrationOtp.getTimesSent());
        if (Boolean.TRUE.equals(userAuth.isEnabled())) {
            throw new BadRequestException(ExceptionMessages.USER_ALREADY_ENABLED, HttpStatus.BAD_REQUEST);
        }
        if (!userAuth.getProvider().equals(Provider.local)) {
            throw new BadRequestException(ExceptionMessages.PROVIDER_NOT_EMAIL, HttpStatus.BAD_REQUEST);
        }
    }

    private void timesSentValidator(int timesSent) {
        if (timesSent > 4) {
            throw new BadRequestException(ExceptionMessages.OTP_RESENT_LIMIT_EXCEED, HttpStatus.BAD_REQUEST);
        }
    }
}
