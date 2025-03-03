package com.gn128.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.gn128.constants.ServiceConstants;
import com.gn128.entity.embeddable.ImageLinks;
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
 * Created_on - December 03 - 2024
 * Created_at - 19:15
 */

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class Profile {

    @Id
    @GeneratedValue(generator = ServiceConstants.RANDOM_UUID)
    @GenericGenerator(name = ServiceConstants.RANDOM_UUID, strategy = ServiceConstants.UUID_STRATEGY)
    private String profileId;

    private String nickname;
    private Date dob;
    private Integer age;
    private String gender;
    private String relationshipGoals;
    private Integer latitude;
    private Integer longitude;
    private String profession;
    private String linkedIdUrl;
    private Double weight;
    private Integer height;
    private String country;
    private String city;
    private String bio;
    private String educationLevel;
    private String interest;
    private Boolean isPicsVerified;
    private String location;

    @ElementCollection
    private List<ImageLinks> imageLinks = new ArrayList<>();

    @Column(unique = true, nullable = false)
    private String userId;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCreated;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateUpdated;

    private String children;
    private String pet;
    private String religion;
    private String personality;
    private String sexuality;
    private String smoking;
    private String relationshipStatus;
    private String drinkings;
    private String starSign;
    private String language;
}
