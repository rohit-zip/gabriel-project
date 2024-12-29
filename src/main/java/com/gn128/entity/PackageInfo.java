package com.gn128.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.gn128.constants.ServiceConstants;
import com.gn128.enums.PackageStatus;
import com.gn128.enums.PackageType;
import com.gn128.enums.SchedulerStatus;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class PackageInfo {

    @Id
    @GeneratedValue(generator = ServiceConstants.RANDOM_UUID)
    @GenericGenerator(name = ServiceConstants.RANDOM_UUID, strategy = ServiceConstants.UUID_STRATEGY)
    private String packageInfoId;

    @Enumerated(EnumType.STRING)
    private PackageType packageType;

    private String userId;

    @Enumerated(EnumType.STRING)
    private PackageStatus packageStatus;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCreated;

    @Temporal(TemporalType.TIMESTAMP)
    private Date expiryDate;

    @Enumerated(EnumType.STRING)
    private SchedulerStatus schedulerStatus;
}
