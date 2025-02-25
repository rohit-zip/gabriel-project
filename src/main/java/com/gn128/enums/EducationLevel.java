package com.gn128.enums;

import com.gn128.exception.payloads.BadRequestException;
import org.springframework.http.HttpStatus;

/**
 * Author - rohit
 * Project - java-backend
 * Package - com.gn128.enums
 * Created_on - December 03 - 2024
 * Created_at - 19:26
 */

public enum EducationLevel {

    NONE("None"),
    PRIMARY_EDUCATION("Primary Education"), // PRIMARY_EDUCATION -> Primary Education
    SECONDRY_EDUCATION("Secondry Education"),
    HIGHER_EDUCATION("Higher Education"),
    VOCATIONAL_TRAINING("Vocational Training"),
    POSTGRADUATE_EDUCATION("Postgraduate Education");

    private final String value;

    EducationLevel(String value) {
        this.value = value;
    }

    public static EducationLevel getByValue(String value) {
        for (EducationLevel educationLevel : values()) {
            if (educationLevel.value.equals(value)) {
                return educationLevel;
            }
        }
        throw new BadRequestException("Educational Level value is incorrect", HttpStatus.BAD_REQUEST);
    }
}
