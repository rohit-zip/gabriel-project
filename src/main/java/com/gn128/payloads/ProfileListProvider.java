package com.gn128.payloads;

import lombok.*;

/**
 * Author - rohit
 * Project - java-backend
 * Package - com.gn128.payloads
 * Created_on - December 04 - 2024
 * Created_at - 20:44
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileListProvider {

    private String filterAndSortField;
    private String type;
    private Boolean searchAllowed;
    private String searchField;
    private Boolean partialSearch;
}
