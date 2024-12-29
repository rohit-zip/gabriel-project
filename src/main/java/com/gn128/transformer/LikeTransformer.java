package com.gn128.transformer;

import com.gn128.authentication.UserPrincipal;
import com.gn128.entity.Like;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Author - rohit
 * Project - java-backend
 * Package - com.gn128.transformer
 * Created_on - December 05 - 2024
 * Created_at - 19:51
 */

@Component
public class LikeTransformer {

    public Like transform(String likedTo, UserPrincipal userPrincipal) {
        return Like
                .builder()
                .likedBy(userPrincipal.getUserId())
                .likedTo(likedTo)
                .userId(userPrincipal.getUserId())
                .dateCreated(new Date())
                .dateUpdated(new Date())
                .build();
    }
}
