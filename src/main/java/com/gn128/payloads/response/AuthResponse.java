package com.gn128.payloads.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.http.ResponseCookie;

import java.util.List;

/**
 * Author - rohit
 * Project - java-backend
 * Package - com.gn128.payloads.response
 * Created_on - November 10 - 2024
 * Created_at - 13:43
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthResponse {

    private String accessToken;
    private String userId;
    private String remoteAddress;
    private List<String> authorities;
    private String email;
    private String username;
    private String message;

    @JsonIgnore
    private String refreshToken;

    @JsonIgnore
    private ResponseCookie cookie;
}
