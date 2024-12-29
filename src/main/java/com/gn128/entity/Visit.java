package com.gn128.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.gn128.constants.ServiceConstants;
import com.gn128.enums.Action;
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
public class Visit {

    @Id
    @GeneratedValue(generator = ServiceConstants.RANDOM_UUID)
    @GenericGenerator(name = ServiceConstants.RANDOM_UUID, strategy = ServiceConstants.UUID_STRATEGY)
    private String visitId;

    private String userId;

    private String visitorId;

    @Enumerated(EnumType.STRING)
    private Action action;

    @Temporal(TemporalType.TIMESTAMP)
    private Date visitDate;
}
