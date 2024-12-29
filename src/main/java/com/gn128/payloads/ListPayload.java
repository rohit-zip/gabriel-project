package com.gn128.payloads;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.gn128.payloads.ListDataPayloads.*;
import lombok.*;

import java.util.List;

/**
 * Author - rohit
 * Project - java-backend
 * Package - com.gn128.payloads
 * Created_on - December 04 - 2024
 * Created_at - 18:49
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class ListPayload {

    private List<Filter> filters;
    private List<Sort> sorts;
    private List<String> search;
    private List<SearchFilter> searchFilters;
    private List<AbsoluteFilter> absoluteFilters;
    private ExcludeFilter excludeFilter;
    private Integer page;
    private Integer size;
}
