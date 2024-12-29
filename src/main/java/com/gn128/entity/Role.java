package com.gn128.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Author - rohit
 * Project - java-backend
 * Package - com.gn128.entity
 * Created_on - November 10 - 2024
 * Created_at - 12:24
 */

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {

    @Id
    private String roleId;
    private String name;
}
