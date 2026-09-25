package com.hugo.humami.service.impl;

import com.hugo.humami.service.GuideAccessEmailSender;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "humami.guides.email", name = "enabled", havingValue = "true")
public class SmtpGuideAccessEmailSender implements GuideAccessEmailSender {
    private final JavaMailSender mailSender;
    private final String sender;

    public SmtpGuideAccessEmailSender(JavaMailSender mailSender,
                                      @Value("${humami.guides.email.from:}") String sender) {
        this.mailSender = mailSender;
        this.sender = sender;
    }

    @Override
    public void sendAccessEmail(String recipient, String guideTitle, String accessUrl) {
        if (sender.isBlank()) throw new IllegalStateException("Guide email sender is not configured");
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(sender);
        message.setTo(recipient);
        message.setSubject("Tu guía de Humami: " + guideTitle);
        message.setText("Ya puedes volver a leer tu guía cuando quieras:\n\n" + accessUrl + "\n\nHumami");
        mailSender.send(message);
    }
}
