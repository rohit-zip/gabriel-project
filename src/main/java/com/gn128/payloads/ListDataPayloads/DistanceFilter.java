package com.gn128.payloads.ListDataPayloads;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

/*
  Developer: Rohit Parihar
  Project: gabriel-project
  GitHub: github.com/rohit-zip
  File: DistanceFilter
 */


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DistanceFilter {

    private int distancePreference;
    private Double longitude;
    private Double latitude;
}
