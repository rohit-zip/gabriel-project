package com.gn128.service;


import com.gn128.authentication.UserPrincipal;
import com.gn128.payloads.response.ListResponse;
import com.gn128.payloads.response.ModuleResponse;

import java.util.concurrent.CompletableFuture;

public interface VisitService {

    CompletableFuture<ModuleResponse> addVisit(String visitorId, String action, UserPrincipal userPrincipal);
    CompletableFuture<ListResponse> getVisits(UserPrincipal userPrincipal);
}
