package com.gn128.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.gn128.constants.ServiceConstants;
import com.gn128.entity.embeddable.PaymentUser;
import com.gn128.enums.PackageType;
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
public class Payment {

    @Id
    @GeneratedValue(generator = ServiceConstants.RANDOM_UUID)
    @GenericGenerator(name = ServiceConstants.RANDOM_UUID, strategy = ServiceConstants.UUID_STRATEGY)
    private String paymentId;

    @Column(nullable = false, unique = true)
    private String transactionId;

    @Column(nullable = false)
    private String userId;

    private Integer amount;
    private String status;

    @Enumerated(EnumType.STRING)
    private PackageType packageType;

    private Integer duration;
    private String currency;
    private String flutterWaveIp;
    private String remoteAddress;
    private Double amountSettled;
    private String message;

    @Embedded
    private PaymentUser paymentUser;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCreated;

}
