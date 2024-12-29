package com.gn128.entity.embeddable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.Embeddable;

/**
 * Author - rohit
 * Project - java-backend
 * Package - com.gn128.entity.embeddable
 * Created_on - December 03 - 2024
 * Created_at - 19:40
 */

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class ImageLinks {

    private String link;
    private String size;
    private String type;
    private String name;
}
