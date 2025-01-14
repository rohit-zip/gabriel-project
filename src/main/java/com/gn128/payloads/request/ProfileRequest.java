package com.gn128.payloads.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.gn128.entity.Profile;
import com.gn128.enums.EducationLevel;
import com.gn128.enums.Gender;
import com.gn128.enums.RelationshipGoal;
import lombok.*;

import java.util.Date;

/*
  Developer: Rohit Parihar
  Project: gabriel-project
  GitHub: github.com/rohit-zip
  File: ProfileRequest
 */


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProfileRequest {

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

    public static void setProfileValues(Profile profile, ProfileRequest profileRequest) {
        profile.setNickname(profileRequest.getNickname());
        profile.setDob(profileRequest.getDob());
        profile.setAge(profileRequest.getAge());
        profile.setGender(profileRequest.getGender());
        profile.setRelationshipGoals(profileRequest.getRelationshipGoals());
        profile.setDistancePreference(profileRequest.getDistancePreference());
        profile.setProfession(profileRequest.getProfession());
        profile.setWeight(profileRequest.getWeight());
        profile.setHeight(profileRequest.getHeight());
        profile.setCountry(profileRequest.getCountry());
        profile.setCity(profileRequest.getCity());
        profile.setBio(profileRequest.getBio());
        profile.setEducationLevel(profileRequest.getEducationLevel());
        profile.setInterest(profileRequest.getInterest());
        profile.setIsPicsVerified(profileRequest.getIsPicsVerified());
        profile.setLocation(profileRequest.getLocation());
        profile.setChildren(profileRequest.getChildren());
        profile.setPet(profileRequest.getPet());
        profile.setReligion(profileRequest.getReligion());
        profile.setPersonality(profileRequest.getPersonality());
        profile.setSexuality(profileRequest.getSexuality());
        profile.setSmoking(profileRequest.getSmoking());
        profile.setRelationshipStatus(profileRequest.getRelationshipStatus());
        profile.setDrinkings(profileRequest.getDrinkings());
        profile.setStarSign(profileRequest.getStarSign());
        profile.setLanguage(profileRequest.getLanguage());
    }

}
