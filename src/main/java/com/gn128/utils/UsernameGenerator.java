package com.gn128.utils;

import com.gn128.dao.repository.UserAuthRepository;
import com.gn128.entity.UserAuth;
import com.gn128.exception.payloads.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Optional;

/**
 * Owner - Rohit Parihar and Bloggios
 * Author - rohit
 * Project - auth-provider-application
 * Package - com.bloggios.auth.provider.utils
 * Created_on - June 17 - 2024
 * Created_at - 17:54
 */

@Component
@RequiredArgsConstructor
public class UsernameGenerator {

    private static final Logger logger = LoggerFactory.getLogger(UsernameGenerator.class);

    private final UserAuthRepository userAuthRepository;

    public String generate(String email) {
        int atIndex = email.lastIndexOf("@");
        String prefixData = email.substring(0, atIndex);
        String initialData = removeSpecialCharacters(prefixData);
        boolean isPresent = true;
        String username = initialData;
        Optional<UserAuth> byUsername = userAuthRepository.findByUsername(username);
        if (byUsername.isEmpty()) {
            isPresent = false;
        }
        int round = 0;
        while (isPresent) {
            if (round == 5) throw new BadRequestException("Exception Occurred while generating Username", HttpStatus.INTERNAL_SERVER_ERROR);
            if (round == 0) {
                logger.warn("Username Generator (Round 0)");
                String characters = "0123456789";
                username = username + generateRandomString(characters, 1);
            } else if (round == 1) {
                logger.warn("Username Generator (Round 1)");
                String characters = "0123456789abcdefghijklmnopqrstuvwxyz";
                username = username + generateRandomString(characters, 2);
            } else if (round >= 2) {
                logger.warn("Username Generator (Round {})", round);
                String characters = "0123456789abcdefghijklmnopqrstuvwxyz-_";
                username = username + generateRandomString(characters, round*2);
            }
            Optional<UserAuth> userDocumentOptional = userAuthRepository.findByUsername(username);
            if (userDocumentOptional.isEmpty()) {
                isPresent = false;
            } else {
                round++;
            }
        }
        return username;
    }

    public static String removeSpecialCharacters(String data) {
        return data.replaceAll("[^a-zA-Z0-9_-]", "");
    }

    public static String generateRandomString(String characters, int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder randomString = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(characters.length());
            randomString.append(characters.charAt(randomIndex));
        }
        return randomString.toString();
    }
}
