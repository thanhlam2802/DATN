package com.example.Notification_service.config;

import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

@Configuration
public class NetworkConfig {

    @PostConstruct
    public void configureNetworkProperties() {
        // Configure system properties for better network handling
        
        // Socket timeout and connection settings
        System.setProperty("sun.net.spi.nameservice.nameservers", "8.8.8.8,8.8.4.4");
        System.setProperty("sun.net.spi.nameservice.provider.1", "dns,sun");
        
        // Network layer timeout settings
        System.setProperty("sun.net.client.defaultConnectTimeout", "30000");
        System.setProperty("sun.net.client.defaultReadTimeout", "30000");
        
        // HTTP connection settings
        System.setProperty("http.keepAlive", "true");
        System.setProperty("http.maxConnections", "10");
        
        // Disable IPv6 if causing issues (sometimes helps with connection resets)
        System.setProperty("java.net.preferIPv4Stack", "true");
        
        // Mail specific network settings
        System.setProperty("mail.smtp.quitwait", "false");
        System.setProperty("mail.smtp.connectionpoolsize", "10");
        System.setProperty("mail.smtp.connectionpooltimeout", "300000");
        
        // JVM network optimizations
        System.setProperty("networkaddress.cache.ttl", "60");
        System.setProperty("networkaddress.cache.negative.ttl", "10");
    }
} 