package com.concord.concordapi.shared.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.MimeMessageHelper;

@Service
public class EmailService {

    @Value("${spring.mail.username}")
    private String EMAIL_SENDER;

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        message.setFrom(EMAIL_SENDER);
        mailSender.send(message);
    }

    public void sendVerificationEmail(String to, String code) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setSubject("Confirmação de Email");
        String htmlContent = getMessgae(code);
        helper.setText(htmlContent, true);
        mailSender.send(message);
    }

    private String getMessgae(String code){
        String htmlContent = "<!DOCTYPE html>" +
                "<html>" +
                "<head><title>Confirmação de Email</title></head>" +
                "<body>" +
                "<h2>Confirmação de Email</h2>" +
                "<p>Olá,</p>" +
                "<p>Seu código de verificação é:</p>" +
                "<h1>" + code + "</h1>" +
                "<p>Por favor, insira este código para concluir seu registro.</p>" +
                "<p>Se você não solicitou este código, ignore este email.</p>" +
                "<hr>" +
                "<footer><p>Este é um email automático, não responda.</p></footer>" +
                "</body>" +
                "</html>";
        return htmlContent;
    }
}
