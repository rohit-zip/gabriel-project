package com.gn128.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Author - rohit
 * Project - java-backend
 * Package - com.gn128.exception
 * Created_on - November 10 - 2024
 * Created_at - 12:35
 */

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ExceptionProvider extends RuntimeException {

    private final String message;
}
