package com.gn128.payloads.ListDataPayloads;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExcludeFilter {

    private String filterKey;
    private List<String> selections;
}
