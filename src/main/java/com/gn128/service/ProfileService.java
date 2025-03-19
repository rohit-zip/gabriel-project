package com.gn128.service;

import com.gn128.authentication.UserPrincipal;
import com.gn128.payloads.ListPayload;
import com.gn128.payloads.request.InitialProfileRequest;
import com.gn128.payloads.request.ProfileRequest;
import com.gn128.payloads.response.ListResponse;
import com.gn128.payloads.response.ModuleResponse;
import com.gn128.payloads.response.ProfileResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Author - rohit
 * Project - java-backend
 * Package - com.gn128.service
 * Created_on - December 03 - 2024
 * Created_at - 20:01
 */

public interface ProfileService {

    ModuleResponse addProfile(InitialProfileRequest initialProfileRequest, UserPrincipal userPrincipal);
    ListResponse list(ListPayload listPayload, UserPrincipal userPrincipal);
    ModuleResponse addUserImages(List<MultipartFile> images, UserPrincipal userPrincipal);
    ProfileResponse getProfile(UserPrincipal userPrincipal);
    ModuleResponse addDetailedOrUpdateProfile(ProfileRequest profileRequest, UserPrincipal userPrincipal);
    ModuleResponse addStatus(List<MultipartFile> images, String message, UserPrincipal userPrincipal);
    ModuleResponse deleteStatus(String statusId, UserPrincipal userPrincipal);
    ListResponse listStatus(String userId, UserPrincipal userPrincipal);
    ListResponse listAllStatus();
    ModuleResponse deleteProfile(UserPrincipal userPrincipal);
}
