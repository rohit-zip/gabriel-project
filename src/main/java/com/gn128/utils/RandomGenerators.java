package com.gn128.utils;

import lombok.experimental.UtilityClass;

import java.security.SecureRandom;
import java.time.LocalDate;

@UtilityClass
public class RandomGenerators {

    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String NUMBERS = "0123456789";
    private static final int ALPHABET_LENGTH = ALPHABET.length();
    private static final int NUMBERS_LENGTH = NUMBERS.length();

    public static String generateRandomImageName(String userId) {
        String month = LocalDate.now().getMonth().toString();
        String[] userIdSplit = userId.split("-");
        return month + "-" + generateRandomString(7) + "-" + userIdSplit[0] + "-" + generateRandomString(7) + "-" + userIdSplit[1] + "-" + generateRandomString(7);
    }

    private static String generateRandomString(int length) {
        SecureRandom secureRandom = new SecureRandom();
        StringBuilder stringBuilder = new StringBuilder(7);

        for (int i = 0; i < length; i++) {
            boolean isDigit = secureRandom.nextBoolean();
            if (isDigit) {
                int randomIndex = secureRandom.nextInt(NUMBERS_LENGTH);
                stringBuilder.append(NUMBERS.charAt(randomIndex));
            } else {
                int randomIndex = secureRandom.nextInt(ALPHABET_LENGTH);
                stringBuilder.append(ALPHABET.charAt(randomIndex));
            }
        }
        return stringBuilder.toString();
    }
}
