package com.gn128.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.gn128.constants.ServiceConstants;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * Author - rohit
 * Project - java-backend
 * Package - com.gn128.entity
 * Created_on - November 10 - 2024
 * Created_at - 14:32
 */

@Entity
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RegistrationOtp {

    @Id
    @GeneratedValue(generator = ServiceConstants.RANDOM_UUID)
    @GenericGenerator(name = ServiceConstants.RANDOM_UUID, strategy = ServiceConstants.UUID_STRATEGY)
    private String otpId;

    private String otp;

    private String email;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateGenerated;

    @Temporal(TemporalType.TIMESTAMP)
    private Date expiry;

    private int timesSent;

    @Column(nullable = false, unique = true)
    private String userId;
}
