package com.gn128.transformer;

/*
  Developer: Rohit Parihar
  Project: gabriel-project
  GitHub: github.com/rohit-zip
  File: ProfileToProfileResponseTransformer
 */

import com.gn128.entity.Profile;
import com.gn128.payloads.response.ProfileResponse;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class ProfileToProfileResponseTransformer {

    private final ModelMapper modelMapper;

    public ProfileToProfileResponseTransformer(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public ProfileResponse transform(Profile profile) {
        return modelMapper.map(profile, ProfileResponse.class);
    }
}
