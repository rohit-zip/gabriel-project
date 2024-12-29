package com.gn128.javamail;

import com.gn128.constants.BeanNameConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Date;

/**
 * Owner - Rohit Parihar and Bloggios
 * Author - rohit
 * Project - bloggios-email-service
 * Package - com.bloggios.email.javamail
 * Created_on - May 05 - 2024
 * Created_at - 16:30
 */

@Component
public abstract class GmailSending<A> {

    private static final Logger logger = LoggerFactory.getLogger(GmailSending.class);

    private final JavaMailSender javaMailSender;

    protected GmailSending(
            @Qualifier(BeanNameConstants.GOOGLE_MAIL_SENDER_BEAN) JavaMailSender javaMailSender
    ) {
        this.javaMailSender = javaMailSender;
    }

    @Async(BeanNameConstants.ASYNC_TASK_INTERNAL_POOL)
    public void sendMail(A a) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        mailingData(mimeMessage, a);
        javaMailSender.send(mimeMessage);
        logger.info("Mail sent using Gmail @{}", new Date());
    }

    public abstract void mailingData(MimeMessage message, A a) throws MessagingException;
}
