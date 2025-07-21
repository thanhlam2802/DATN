package com.example.Notification_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
@Profile("oauth2") // Chỉ kích hoạt khi profile oauth2 được bật
public class GmailOAuth2Config {

    // Cấu hình này sẽ được sử dụng nếu App Password không hoạt động
    // Để sử dụng: thêm spring.profiles.active=oauth2 vào application.properties
    
    @Bean
    public JavaMailSender gmailOAuth2MailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);
        
        // Không set username/password - sẽ dùng OAuth2
        
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        props.put("mail.smtp.ssl.trust", "*");
        props.put("mail.smtp.timeout", "25000");
        props.put("mail.smtp.connectiontimeout", "25000");
        
        // OAuth2 specific properties
        props.put("mail.smtp.auth.mechanisms", "XOAUTH2");
        props.put("mail.smtp.auth.plain.disable", "true");
        
        return mailSender;
    }
} 