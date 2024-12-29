package com.gn128.service.implementation;

import com.gn128.authentication.UserPrincipal;
import com.gn128.constants.BeanNameConstants;
import com.gn128.dao.repository.UserAuthRepository;
import com.gn128.entity.UserAuth;
import com.gn128.exception.payloads.BadRequestException;
import com.gn128.payloads.request.ChangePasswordRequest;
import com.gn128.payloads.response.ModuleResponse;
import com.gn128.service.UserService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Service
public class UserServiceImplementation implements UserService {

    private final UserAuthRepository userAuthRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImplementation(UserAuthRepository userAuthRepository, PasswordEncoder passwordEncoder) {
        this.userAuthRepository = userAuthRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Async(BeanNameConstants.ASYNC_TASK_EXTERNAL_POOL)
    public CompletableFuture<ModuleResponse> changePassword(ChangePasswordRequest changePasswordRequest, UserPrincipal userPrincipal) {
        if (Objects.isNull(changePasswordRequest))
            throw new BadRequestException("Request cannot be null");
        if (Objects.isNull(changePasswordRequest.getNewPassword()) || ObjectUtils.isEmpty(changePasswordRequest.getNewPassword())) {
            throw new BadRequestException("New password cannot be null or empty");
        }
        if (Objects.isNull(changePasswordRequest.getOldPassword()) || ObjectUtils.isEmpty(changePasswordRequest.getOldPassword())) {
            throw new BadRequestException("Old password cannot be null or empty");
        }
        if (changePasswordRequest.getNewPassword().equals(changePasswordRequest.getOldPassword())) {
            throw new BadRequestException("New Password should be different from Old Password");
        }
        UserAuth userAuth = userAuthRepository.findById(userPrincipal.getUserId())
                .orElseThrow(()-> new BadRequestException("User not found with User Id"));
        if (!passwordEncoder.matches(changePasswordRequest.getOldPassword(), userAuth.getPassword())) {
            throw new BadRequestException("Old password is incorrect");
        }
        userAuth.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        userAuth.setLastPasswordChanged(new Date());
        userAuth.setLastUpdated(new Date());
        userAuthRepository.save(userAuth);
        return CompletableFuture.completedFuture(
                ModuleResponse
                        .builder()
                        .message("Password Updated")
                        .userId(userPrincipal.getUserId())
                        .build()
        );
    }
}
