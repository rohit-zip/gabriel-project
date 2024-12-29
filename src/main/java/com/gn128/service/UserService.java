package com.gn128.service;

import com.gn128.authentication.UserPrincipal;
import com.gn128.payloads.request.ChangePasswordRequest;
import com.gn128.payloads.response.ModuleResponse;

import java.util.concurrent.CompletableFuture;

public interface UserService {
    CompletableFuture<ModuleResponse> changePassword(ChangePasswordRequest changePasswordRequest, UserPrincipal userPrincipal);
}
