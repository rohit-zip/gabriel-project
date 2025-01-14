package com.gn128.processor.mail;

import com.gn128.constants.EnvironmentConstants;
import com.gn128.entity.ForgetPassword;
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

@Component
public class ForgetPasswordMailSendingProcessor extends GmailSending<ForgetPassword> {

    private final TemplateEngine templateEngine;
    private final Environment environment;

    protected ForgetPasswordMailSendingProcessor(JavaMailSender javaMailSender, TemplateEngine templateEngine, Environment environment) {
        super(javaMailSender);
        this.templateEngine = templateEngine;
        this.environment = environment;
    }

    @Override
    public void mailingData(MimeMessage message, ForgetPassword forgetPassword) throws MessagingException {
        String context = getContext(forgetPassword);
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(Objects.requireNonNull(environment.getProperty(EnvironmentConstants.GOOGLE_MAIL_USERNAME)));
        helper.setSubject("GN128 | Forget Password OTP");
        helper.setText(context, true);
        helper.setTo(forgetPassword.getEmail());
    }

    private String getContext(ForgetPassword forgetPasswordOtpMailEvent) {
        Context context = new Context();
        context.setVariable("otpPayload", forgetPasswordOtpMailEvent);
        return templateEngine.process("ForgetPasswordOtp", context);
    }
}

