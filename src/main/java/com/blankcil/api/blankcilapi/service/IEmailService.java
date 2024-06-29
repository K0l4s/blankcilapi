package com.blankcil.api.blankcilapi.service;

public interface IEmailService {
    void sendVerificationMail(String email, String code);
    void sendWelcomeMessage(String email, String fullName);
}
