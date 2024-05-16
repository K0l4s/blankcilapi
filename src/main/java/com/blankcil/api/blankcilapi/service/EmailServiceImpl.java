package com.blankcil.api.blankcilapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements IEmailService{
    @Autowired
    private JavaMailSender javaMailSender;
    @Override
    public boolean sendEmail(String from, String to, String subject,String body){
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("alotramilktea@gmail.com");
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            javaMailSender.send(message);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
