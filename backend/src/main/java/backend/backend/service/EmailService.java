package backend.backend.service;

import backend.backend.dto.email.SendEmailRequestDto;

public interface EmailService {
    void sendEmail(SendEmailRequestDto sendEmailRequestDto);
}
