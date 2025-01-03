package com.concord.concordapi.shared.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Primary;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@Primary
public class MailServiceMock extends EmailService{
    private final Map<String, String> sentEmails = new HashMap<>();

    public MailServiceMock(JavaMailSender mailSender) {
        super(mailSender);
        System.out.println("fui criado");
    }

    @Override
    public void sendEmail(String to, String subject, String body) {
        System.out.println("tentando aki enviar o email");
        sentEmails.put(to, body); // Salva o e-mail enviado para verificação posterior
    }

    public String getEmailBody(String to) {
        return sentEmails.get(to);
    }

}
