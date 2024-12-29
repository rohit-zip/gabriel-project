package com.gn128.enums;

import com.gn128.exception.payloads.BadRequestException;
import org.springframework.http.HttpStatus;

/**
 * Author - rohit
 * Project - java-backend
 * Package - com.gn128.enums
 * Created_on - December 03 - 2024
 * Created_at - 19:31
 */

public enum Gender {

    MALE("Male"),
    FEMALE("Female"),
    NOT_SAY("Rather not to say");

    private final String value;

    Gender(String value) {
        this.value = value;
    }

    public static Gender getByValue(String value) {
        for (Gender gender : values()) {
            if (gender.value.equals(value)) {
                return gender;
            }
        }
        throw new BadRequestException("Gender value is incorrect", HttpStatus.BAD_REQUEST);
    }
}
