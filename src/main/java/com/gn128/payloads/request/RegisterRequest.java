package com.gn128.payloads.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.gn128.constants.ServiceConstants;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * Author - rohit
 * Project - java-backend
 * Package - com.gn128.payloads.request
 * Created_on - November 10 - 2024
 * Created_at - 14:05
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class RegisterRequest {

    @NotBlank
    @Pattern(regexp = ServiceConstants.EMAIL_REGEX)
    private String email;

    @NotBlank
    @Pattern(regexp = ServiceConstants.PASSWORD_REGEX)
    private String password;
}
