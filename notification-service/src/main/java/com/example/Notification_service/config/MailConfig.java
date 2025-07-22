package com.example.Notification_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfig {

    @Value("${spring.mail.host}")
    private String host;
    
    @Value("${spring.mail.port}")
    private int port;
    
    @Value("${spring.mail.username}")
    private String username;
    
    @Value("${spring.mail.password}")
    private String password;

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(port);
        mailSender.setUsername(username);
        mailSender.setPassword(password);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        props.put("mail.smtp.ssl.trust", "*");
        props.put("mail.smtp.ssl.checkserveridentity", "false");
        
        // Enhanced timeout settings to handle connection issues
        props.put("mail.smtp.timeout", "30000");
        props.put("mail.smtp.connectiontimeout", "30000");
        props.put("mail.smtp.writetimeout", "30000");
        
        // Connection stability settings
        props.put("mail.smtp.connectionpoolsize", "10");
        props.put("mail.smtp.connectionpooltimeout", "300000");
        
        // Additional stability configurations
        props.put("mail.smtp.quitwait", "false");
        props.put("mail.smtp.socketFactory.fallback", "true");
        
        // Trust all certificates for Gmail specifically
        props.put("mail.smtp.ssl.trust", "*");
        
        return mailSender;
    }
} 