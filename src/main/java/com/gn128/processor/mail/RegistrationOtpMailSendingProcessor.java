package com.gn128.processor.mail;

import com.gn128.constants.EnvironmentConstants;
import com.gn128.entity.RegistrationOtp;
import com.gn128.javamail.GmailSending;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Objects;

/**
 * Author - rohit
 * Project - java-backend
 * Package - com.gn128.processor
 * Created_on - November 10 - 2024
 * Created_at - 14:57
 */

@Component
public class RegistrationOtpMailSendingProcessor extends GmailSending<RegistrationOtp> {

    private final TemplateEngine templateEngine;
    private final Environment environment;

    protected RegistrationOtpMailSendingProcessor(JavaMailSender javaMailSender, TemplateEngine templateEngine, Environment environment) {
        super(javaMailSender);
        this.templateEngine = templateEngine;
        this.environment = environment;
    }

    @Override
    public void mailingData(MimeMessage message, RegistrationOtp registrationOtp) throws MessagingException {
        String context = getContext(registrationOtp);
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(Objects.requireNonNull(environment.getProperty(EnvironmentConstants.GOOGLE_MAIL_USERNAME)));
        helper.setSubject("GN128 | Registration OTP");
        helper.setText(context, true);
        helper.setTo(registrationOtp.getEmail());
    }

    private String getContext(RegistrationOtp registerOtpMailEvent) {
        Context context = new Context();
        context.setVariable("otpPayload", registerOtpMailEvent);
        return templateEngine.process("RegistrationOtp", context);
    }
}
