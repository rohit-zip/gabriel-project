package com.gn128.service.implementation;

import com.gn128.authentication.UserPrincipal;
import com.gn128.constants.BeanNameConstants;
import com.gn128.constants.ExceptionMessages;
import com.gn128.dao.repository.ProfileRepository;
import com.gn128.dao.repository.UserAuthRepository;
import com.gn128.dao.repository.VisitRepository;
import com.gn128.entity.Profile;
import com.gn128.entity.UserAuth;
import com.gn128.entity.Visit;
import com.gn128.enums.Action;
import com.gn128.exception.payloads.BadRequestException;
import com.gn128.payloads.response.ModuleResponse;
import com.gn128.service.LikeService;
import com.gn128.service.VisitService;
import com.gn128.utils.AsyncUtils;
import com.gn128.utils.ValueCheckerUtil;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class VisitServiceImplementation implements VisitService {

    private final VisitRepository visitRepository;
    private final UserAuthRepository userAuthRepository;
    private final ProfileRepository profileRepository;
    private final LikeService likeService;

    public VisitServiceImplementation(VisitRepository visitRepository, UserAuthRepository userAuthRepository, ProfileRepository profileRepository, LikeService likeService) {
        this.visitRepository = visitRepository;
        this.userAuthRepository = userAuthRepository;
        this.profileRepository = profileRepository;
        this.likeService = likeService;
    }

    @Override
    @Async(BeanNameConstants.ASYNC_TASK_EXTERNAL_POOL)
    public CompletableFuture<ModuleResponse> addVisit(String visitorId, String action, UserPrincipal userPrincipal) {
        ValueCheckerUtil.isValidUUID(visitorId);
        CompletableFuture<UserAuth> userAuthCompletableFuture = CompletableFuture.supplyAsync(() -> userAuthRepository.findById(userPrincipal.getUserId())
                .orElseThrow(() -> new BadRequestException(ExceptionMessages.USER_NOT_FOUND_ID, HttpStatus.BAD_REQUEST)));
        CompletableFuture<Optional<Profile>> profileOptionalCompletableFuture = CompletableFuture.supplyAsync(() -> profileRepository.findByUserId(userPrincipal.getUserId()));
        AsyncUtils.getAsyncResult(CompletableFuture.allOf(userAuthCompletableFuture, profileOptionalCompletableFuture));
        if (Boolean.FALSE.equals(userAuthCompletableFuture.join().isEnabled()))
            throw new BadRequestException("Visitor user is not Enabled", HttpStatus.BAD_REQUEST);
        if (profileOptionalCompletableFuture.join().isEmpty())
            throw new BadRequestException("Visitor User hasn't created their profile");
        Visit visit = Visit
                .builder()
                .userId(userPrincipal.getUserId())
                .visitorId(visitorId)
                .action(Action.valueOf(action.toUpperCase()))
                .visitDate(Date.from(Instant.now()))
                .build();
        Visit visitResponse = visitRepository.save(visit);
        if (action.equalsIgnoreCase(Action.LIKE.toString()) || action.equalsIgnoreCase(Action.DISLIKE.toString())) {
            likeService.addLike(visitorId, action, userPrincipal);
        }
        return CompletableFuture.completedFuture(
                ModuleResponse
                        .builder()
                        .message("User Visited")
                        .userId(userPrincipal.getUserId())
                        .id(visitResponse.getVisitId())
                        .build()
        );
    }
}
