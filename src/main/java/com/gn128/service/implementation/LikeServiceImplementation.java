package com.gn128.service.implementation;

import com.gn128.authentication.UserPrincipal;
import com.gn128.dao.repository.LikeRepository;
import com.gn128.entity.Like;
import com.gn128.enums.Action;
import com.gn128.payloads.response.ListResponse;
import com.gn128.payloads.response.ModuleResponse;
import com.gn128.service.LikeService;
import com.gn128.transformer.LikeTransformer;
import com.gn128.utils.ValueCheckerUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Author - rohit
 * Project - java-backend
 * Package - com.gn128.service.implementation
 * Created_on - December 05 - 2024
 * Created_at - 19:44
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class LikeServiceImplementation implements LikeService {

    private final LikeTransformer likeTransformer;
    private final LikeRepository likeRepository;

    @Override
    public ModuleResponse addLike(String likedTo, String action, UserPrincipal userPrincipal) {
        long startTime = System.currentTimeMillis();
        ValueCheckerUtil.isValidUUID(likedTo);
        Like like = likeTransformer.transform(likedTo, userPrincipal);
        like.setAction(Action.valueOf(action));
        Like likeResponse = likeRepository.save(like);
        log.info("Execution Time (Add Like) : {}ms", System.currentTimeMillis() - startTime);
        return ModuleResponse
                .builder()
                .message("Liked")
                .userId(userPrincipal.getUserId())
                .id(likeResponse.getLikeId())
                .build();
    }

    @Override
    public ListResponse getUserLikes(UserPrincipal userPrincipal) {
        List<Like> likes = likeRepository.findAllByUserId(userPrincipal.getUserId());
        return ListResponse
                .builder()
                .object(likes)
                .size(likes.size())
                .page(0)
                .build();
    }
}
