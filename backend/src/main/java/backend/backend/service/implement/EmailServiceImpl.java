package backend.backend.service.implement;

import backend.backend.dto.email.SendEmailRequestDto;
import backend.backend.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String from;

    @Override
    public void sendEmail(SendEmailRequestDto sendEmailRequestDto) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(from);
            helper.setTo(sendEmailRequestDto.getTo());
            helper.setSubject(sendEmailRequestDto.getSubject());
            helper.setText(sendEmailRequestDto.getBody(), true); // true enables HTML
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

    }
}
