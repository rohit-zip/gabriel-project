package com.gn128.service;

import com.gn128.authentication.UserPrincipal;
import com.gn128.payloads.response.ListResponse;
import com.gn128.payloads.response.ModuleResponse;

/**
 * Author - rohit
 * Project - java-backend
 * Package - com.gn128.service
 * Created_on - December 05 - 2024
 * Created_at - 19:42
 */

public interface LikeService {

    ModuleResponse addLike(String likedTo, String action, UserPrincipal userPrincipal);
    ListResponse getUserLikes(UserPrincipal userPrincipal);
}
