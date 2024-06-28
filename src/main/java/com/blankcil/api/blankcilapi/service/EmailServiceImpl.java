package com.blankcil.api.blankcilapi.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.UnsupportedEncodingException;

@Service
public class EmailServiceImpl implements IEmailService{
    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String from;

    private final String frontendDomain = "http://localhost:3000";
//    private final String frontendDomain = "https://blankcil.vercel.app";

    private void sendEmail(String to, String subject,String content){
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        try {
            helper.setFrom(from, "Blankcil");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);
            javaMailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendVerificationMail(String email, String code) {
        String subject = "Blankcil Verification";

        // use the Frontend domain when click on button in email body
        String verificationUrl = frontendDomain + "/verify?code=" + code;

        Context context = new Context();
        context.setVariable("verificationUrl", verificationUrl);
        context.setVariable("verificationCode", code);

        String htmlContent = templateEngine.process("email/verification", context);

        sendEmail(email, subject, htmlContent);
    }

    @Override
    public void sendWelcomeMessage(String email, String fullName) {
        String subject = "Welcome to Blankcil";

        Context context = new Context();
        context.setVariable("fullName", fullName);

        String htmlContent = templateEngine.process("email/welcome", context);

        sendEmail(email, subject, htmlContent);
    }
}
