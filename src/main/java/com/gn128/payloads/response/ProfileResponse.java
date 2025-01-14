package com.gn128.payloads.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.gn128.entity.embeddable.ImageLinks;
import com.gn128.enums.EducationLevel;
import com.gn128.enums.Gender;
import com.gn128.enums.RelationshipGoal;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/*
  Developer: Rohit Parihar
  Project: gabriel-project
  GitHub: github.com/rohit-zip
  File: ProfileResponse
 */


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProfileResponse {

    private String profileId;
    private String nickname;
    private Date dob;
    private Integer age;
    private Gender gender;
    private RelationshipGoal relationshipGoals;
    private Integer distancePreference;
    private String profession;
    private Double weight;
    private Integer height;
    private String country;
    private String city;
    private String bio;
    private EducationLevel educationLevel;
    private String interest;
    private Boolean isPicsVerified;
    private String location;
    private List<ImageLinks> imageLinks = new ArrayList<>();
    private String userId;
    private Date dateCreated;
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
