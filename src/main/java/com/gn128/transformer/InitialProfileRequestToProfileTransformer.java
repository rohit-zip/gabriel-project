package com.gn128.transformer;

import com.gn128.entity.Profile;
import com.gn128.enums.EducationLevel;
import com.gn128.payloads.request.InitialProfileRequest;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Author - rohit
 * Project - java-backend
 * Package - com.gn128.transformer
 * Created_on - December 03 - 2024
 * Created_at - 20:20
 */

@Component
public class InitialProfileRequestToProfileTransformer {

    public Profile transform(InitialProfileRequest initialProfileRequest, String userId) {
        return Profile
                .builder()
                .profession(initialProfileRequest.getProfession())
                .weight(initialProfileRequest.getWeight())
                .height(initialProfileRequest.getHeight())
                .country(initialProfileRequest.getCountry())
                .city(initialProfileRequest.getCity())
                .bio(initialProfileRequest.getBio())
                .educationLevel(EducationLevel.getByValue(initialProfileRequest.getEducationLevel()))
                .dateCreated(new Date())
                .userId(userId)
                .build();
    }
}
