package com.gn128.payloads.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * Author - rohit
 * Project - java-backend
 * Package - com.gn128.payloads.request
 * Created_on - December 03 - 2024
 * Created_at - 19:57
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class InitialProfileRequest {

    @NotBlank(message = "Profession cannot be blank or null")
    @Size(max = 500)
    private String profession;
    private Double weight;
    private Integer height;
    private String country;
    private String city;
    private String bio;
    private String educationLevel;
    private String linkedInUrl;
}
