package com.gn128.properties;

import lombok.experimental.UtilityClass;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

/**
 * Owner - Rohit Parihar and Bloggios
 * Author - rohit
 * Project - bloggios-email-service
 * Package - com.bloggios.email.properties
 * Created_on - May 20 - 2024
 * Created_at - 20:24
 */

@UtilityClass
public class MailProperties {

    public static void mailProperties(JavaMailSenderImpl javaMailSender) {
        Properties props = javaMailSender.getJavaMailProperties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
    }
}
