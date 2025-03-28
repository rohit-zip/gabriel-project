package com.gn128.service.implementation;

import com.gn128.authentication.UserPrincipal;
import com.gn128.constants.EnvironmentConstants;
import com.gn128.constants.ExceptionMessages;
import com.gn128.constants.ServiceConstants;
import com.gn128.dao.pgsql.PgSQLProfile;
import com.gn128.dao.repository.ProfileRepository;
import com.gn128.dao.repository.StatusRepository;
import com.gn128.dao.repository.VisitRepository;
import com.gn128.entity.Profile;
import com.gn128.entity.Status;
import com.gn128.entity.Visit;
import com.gn128.entity.embeddable.ImageLinks;
import com.gn128.exception.payloads.BadRequestException;
import com.gn128.payloads.ListDataPayloads.ExcludeFilter;
import com.gn128.payloads.ListPayload;
import com.gn128.payloads.record.UploadImagePayloadRecord;
import com.gn128.payloads.request.InitialProfileRequest;
import com.gn128.payloads.request.ProfileRequest;
import com.gn128.payloads.response.ListResponse;
import com.gn128.payloads.response.ModuleResponse;
import com.gn128.payloads.response.ProfileResponse;
import com.gn128.processor.UploadImagesLinkProcessor;
import com.gn128.processor.listprocessor.ProfileListProcessor;
import com.gn128.service.ProfileService;
import com.gn128.transformer.InitialProfileRequestToProfileTransformer;
import com.gn128.transformer.ProfileToProfileResponseTransformer;
import com.gn128.utils.AsyncUtils;
import com.gn128.validator.implementation.ImagesListValidator;
import com.gn128.validator.implementation.ListPayloadExhibitor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Author - rohit
 * Project - java-backend
 * Package - com.gn128.service.implementation
 * Created_on - December 03 - 2024
 * Created_at - 20:02
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfileServiceImplementation implements ProfileService {

    private final ProfileRepository profileRepository;
    private final InitialProfileRequestToProfileTransformer initialProfileRequestToProfileTransformer;
    private final ListPayloadExhibitor listPayloadExhibitor;
    private final ProfileListProcessor profileListProcessor;
    private final PgSQLProfile profilePgsqlBuilderImplementation;
    private final UploadImagesLinkProcessor uploadImagesLinkProcessor;
    private final Environment environment;
    private final ImagesListValidator imagesListValidator;
    private final ProfileToProfileResponseTransformer profileToProfileResponseTransformer;
    private final VisitRepository visitRepository;
    private final StatusRepository statusRepository;

    @Override
    public ModuleResponse addProfile(InitialProfileRequest initialProfileRequest, UserPrincipal userPrincipal) {
        long startTime = System.currentTimeMillis();
        String userId = userPrincipal.getUserId();
        Optional<Profile> byUserId = profileRepository.findByUserId(userId);
        if (byUserId.isPresent()) throw new BadRequestException(ExceptionMessages.PROFILE_ALREADY_ADDED, HttpStatus.BAD_REQUEST);
        Profile profile = initialProfileRequestToProfileTransformer.transform(initialProfileRequest, userId);
        Profile profileResponse = profileRepository.saveAndFlush(profile);
        log.info("Execution Time (Add Profile) : {}ms", System.currentTimeMillis() - startTime);
        return ModuleResponse
                .builder()
                .message("Profile Added successfully to Database")
                .userId(profileResponse.getUserId())
                .id(profileResponse.getProfileId())
                .build();
    }

    @Override
    public ListResponse list(ListPayload listPayload, UserPrincipal userPrincipal) {
        listPayloadExhibitor.validateAll(listPayload);
        CompletableFuture<List<Visit>> visitCompletableFuture = CompletableFuture.supplyAsync(() -> visitRepository.findAllByUserId(userPrincipal.getUserId()));
        ListPayload processedListPayload = profileListProcessor.initProcess(listPayload);
        List<Visit> visitList = AsyncUtils.getAsyncResult(visitCompletableFuture);
        List<String> userIdList = visitList.stream().map(Visit::getUserId).collect(Collectors.toList());
        userIdList.add(userPrincipal.getUserId());
        log.info("Visit List: {}", visitList);
        if (!visitList.isEmpty()) {
            ExcludeFilter excludeFilter = ExcludeFilter
                    .builder()
                    .filterKey("userId")
                    .selections(userIdList)
                    .build();
            processedListPayload.setExcludeFilter(excludeFilter);
        } else {
            ExcludeFilter excludeFilter = ExcludeFilter
                    .builder()
                    .filterKey("userId")
                    .selections(List.of(userPrincipal.getUserId()))
                    .build();
            processedListPayload.setExcludeFilter(excludeFilter);
        }
        List<Profile> responseData = profilePgsqlBuilderImplementation.build(processedListPayload).getResultList();
        Long totalRecords = profilePgsqlBuilderImplementation.getTotalRecords(processedListPayload);
        return ListResponse
                .builder()
                .object(responseData)
                .totalElements(totalRecords)
                .size(processedListPayload.getSize())
                .page(processedListPayload.getPage())
                .build();
    }

    @Override
    public ModuleResponse addUserImages(List<MultipartFile> images, UserPrincipal userPrincipal) {
        long startTime = System.currentTimeMillis();
        imagesListValidator.validate(images);
        Profile profile = profileRepository.findByUserId(userPrincipal.getUserId())
                .orElseThrow(() -> new BadRequestException("Profile not found using User Id", HttpStatus.BAD_REQUEST));
        List<ImageLinks> profileImageLinks = profile.getImageLinks();
        if (Objects.nonNull(profileImageLinks) && !CollectionUtils.isEmpty(profileImageLinks)) {
            int size = profileImageLinks.size();
            if (images.size() + size > 9) {
                throw new BadRequestException(size + "Images are already present. Now you can only add " + (9 - size) + " Images", HttpStatus.BAD_REQUEST);
            }
        }
        UploadImagePayloadRecord uploadImagePayloadRecord = new UploadImagePayloadRecord(
                environment.getProperty(EnvironmentConstants.PROFILE_IMAGES_PATH),
                userPrincipal.getUserId(),
                images,
                ServiceConstants.UPLOAD_PROFILE
        );
        List<ImageLinks> imageLinks = uploadImagesLinkProcessor.process(uploadImagePayloadRecord);
        profile.getImageLinks().addAll(imageLinks);
        profile.setDateUpdated(new Date());
        Profile profileResponse = profileRepository.save(profile);
        log.info("Execution Time (Add User Images) : {}ms", System.currentTimeMillis() - startTime);
        return ModuleResponse.builder().message("Images Uploaded").id(profileResponse.getProfileId()).build();
    }

    @Override
    public ProfileResponse getProfile(UserPrincipal userPrincipal) {
        Profile profile = profileRepository.findByUserId(userPrincipal.getUserId())
                .orElseThrow(() -> new BadRequestException("Profile not found using User Id", HttpStatus.BAD_REQUEST));
        return profileToProfileResponseTransformer.transform(profile);
    }

    @Override
    public ModuleResponse addDetailedOrUpdateProfile(ProfileRequest profileRequest, UserPrincipal userPrincipal) {
        Profile profile = profileRepository.findByUserId(userPrincipal.getUserId())
                .orElseThrow(() -> new BadRequestException("Profile not found using User Id", HttpStatus.BAD_REQUEST));
        ProfileRequest.setProfileValues(profile, profileRequest);
        Profile profileResponse = profileRepository.save(profile);
        return ModuleResponse
                .builder()
                .message("Profile Updated to server")
                .userId(userPrincipal.getUserId())
                .id(profileResponse.getProfileId())
                .build();
    }

    @Override
    public ModuleResponse addStatus(List<MultipartFile> images, String message, UserPrincipal userPrincipal) {
        imagesListValidator.validate(images);
        Profile profile = profileRepository.findByUserId(userPrincipal.getUserId())
                .orElseThrow(() -> new BadRequestException("Profile not found using User Id", HttpStatus.BAD_REQUEST));
        List<ImageLinks> profileImageLinks = profile.getImageLinks();
        if (Objects.nonNull(profileImageLinks) && !CollectionUtils.isEmpty(profileImageLinks)) {
            int size = profileImageLinks.size();
            if (images.size() + size > 9) {
                throw new BadRequestException(size + "Images are already present. Now you can only add " + (9 - size) + " Images", HttpStatus.BAD_REQUEST);
            }
        }
        UploadImagePayloadRecord uploadImagePayloadRecord = new UploadImagePayloadRecord(
                environment.getProperty(EnvironmentConstants.PROFILE_IMAGES_PATH),
                userPrincipal.getUserId(),
                images,
                ServiceConstants.UPLOAD_PROFILE
        );
        List<ImageLinks> imageLinks = uploadImagesLinkProcessor.process(uploadImagePayloadRecord);
        Status status = Status
                .builder()
                .profileId(profile.getProfileId())
                .userId(userPrincipal.getUserId())
                .imageLinks(imageLinks)
                .message(message)
                .build();
        Status statusResponse = statusRepository.save(status);
        return ModuleResponse
                .builder()
                .message("Status Added")
                .userId(userPrincipal.getUserId())
                .id(statusResponse.getStatusId())
                .build();
    }

    @Override
    public ModuleResponse deleteStatus(String statusId, UserPrincipal userPrincipal) {
        Status status = statusRepository.findById(statusId)
                .orElseThrow(() -> new BadRequestException("Status not found using Status Id", HttpStatus.BAD_REQUEST));
        statusRepository.delete(status);
        return ModuleResponse
                .builder()
                .message("Status Deleted")
                .userId(userPrincipal.getUserId())
                .id(statusId)
                .build();
    }

    @Override
    public ListResponse listStatus(String userId, UserPrincipal userPrincipal) {
        List<Status> status = statusRepository.findAllByUserId(userId != null ? userId : userPrincipal.getUserId());
        return ListResponse
                .builder()
                .object(status)
                .page(0)
                .size(status.size())
                .build();
    }

    @Override
    public ListResponse listAllStatus() {
        List<Status> status = statusRepository.findAll();
        return ListResponse
                .builder()
                .object(status)
                .page(0)
                .size(status.size())
                .build();
    }

    @Override
    public ModuleResponse deleteProfile(UserPrincipal userPrincipal) {
        log.info("Delete Profile : {}", userPrincipal.getUserId());
        profileRepository.deleteByUserId(userPrincipal.getUserId());
        return ModuleResponse
                .builder()
                .message("Profile Deleted")
                .build();
    }
}
