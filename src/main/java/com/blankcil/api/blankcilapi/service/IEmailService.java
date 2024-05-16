package com.blankcil.api.blankcilapi.service;

public interface IEmailService {
    boolean sendEmail(String from, String to, String subject, String body);
}
