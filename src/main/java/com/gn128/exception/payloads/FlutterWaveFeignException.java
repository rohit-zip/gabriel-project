package com.gn128.exception.payloads;

import com.gn128.exception.ExceptionProvider;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class FlutterWaveFeignException extends ExceptionProvider {

    private final HttpStatus httpStatus;

    public FlutterWaveFeignException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
}
