package com.concord.concordapi.shared.service;

import org.springframework.stereotype.Component;
import org.springframework.test.context.ActiveProfiles;

import jakarta.mail.MessagingException;

@Component
@ActiveProfiles("test")
public class EmailServiceTest extends EmailService {

    private String lastCode;
    private String lastResetLink;

    public EmailServiceTest() {
        super(null);
    }

    @Override
    public void sendEmail(String to, String subject, String body) {
        System.out.printf("sendEmail: to=%s, subject=%s, body=%s%n", to, subject, body);
    }

    @Override
    public void sendVerificationEmail(String to, String code) throws MessagingException {
        System.out.printf("sendVerificationEmail: to=%s, code=%s%n", to, code);
        this.lastCode = code;
    }

    @Override
    public void sendForgotPasswordEmail(String to, String resetLink) throws MessagingException {
        System.out.printf("sendForgotPasswordEmail: to=%s, resetLink=%s%n", to, resetLink);
        this.lastResetLink = resetLink;
    }

    public String getCode(){
        return this.lastCode;
    }
    public String getResetCode(){
        return this.lastResetLink.split("token=")[1];
    }
}