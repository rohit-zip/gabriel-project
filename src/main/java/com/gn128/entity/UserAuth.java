package com.gn128.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.gn128.constants.ServiceConstants;
import com.gn128.enums.Provider;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Author - rohit
 * Project - java-backend
 * Package - com.gn128.entity
 * Created_on - November 10 - 2024
 * Created_at - 12:21
 */

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserAuth {

    @Id
    @GeneratedValue(generator = ServiceConstants.RANDOM_UUID)
    @GenericGenerator(name = ServiceConstants.RANDOM_UUID, strategy = ServiceConstants.UUID_STRATEGY)
    private String userId;

    @Column(unique = true)
    private String oauthId;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true, nullable = false)
    private String username;

    private String password;


    @Enumerated(EnumType.STRING)
    private Provider provider;

    private String apiVersion;
    private int timesDisabled = 0;

    private boolean isEnabled;
    private boolean isAccountNonExpired;
    private boolean isAccountNonLocked;
    private boolean isCredentialsNonExpired;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateRegistered;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateEnabled;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdated;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastPasswordChanged;

    private String remoteAddress;

    private boolean isProfileAdded;

    private boolean isBadge;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(joinColumns = @JoinColumn(name = "UserAuth", referencedColumnName = "userId"), inverseJoinColumns = @JoinColumn(name = "Role", referencedColumnName = "roleId"))
    private List<Role> roles = new ArrayList<>();


}
