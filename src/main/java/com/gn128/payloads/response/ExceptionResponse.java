package com.gn128.payloads.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

/**
 * Author - rohit
 * Project - java-backend
 * Package - com.gn128.payloads.response
 * Created_on - November 10 - 2024
 * Created_at - 12:38
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ExceptionResponse {

    private String status;
    private String message;
}

