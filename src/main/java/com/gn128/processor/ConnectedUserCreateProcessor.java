package com.gn128.processor;

/*
  Developer: Rohit Parihar
  Project: gabriel-project
  GitHub: github.com/rohit-zip
  File: ConnectedUserCreateProcessor
 */

import com.gn128.dao.repository.ConnectedUserRepository;
import com.gn128.entity.ConnectedUser;
import com.gn128.entity.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;

@Component
@RequiredArgsConstructor
public class ConnectedUserCreateProcessor {

    private final ConnectedUserRepository connectedUserRepository;

    public void process(UserStatus userStatus) {
        ConnectedUser connectedUser = ConnectedUser
                .builder()
                .userId(userStatus.getUserId())
                .userStatusId(userStatus.getUserStatusId())
                .sessionId(userStatus.getSessionId())
                .localTime(LocalTime.now())
                .localDate(LocalDate.now())
                .remoteAddress(userStatus.getRemoteAddress())
                .build();
        connectedUserRepository.save(connectedUser);
    }
}
