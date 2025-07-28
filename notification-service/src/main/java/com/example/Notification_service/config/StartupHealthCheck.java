package com.example.Notification_service.config;

import com.example.Notification_service.service.ConnectionHealthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StartupHealthCheck implements CommandLineRunner {

    private final ConnectionHealthService connectionHealthService;

    @Override
    public void run(String... args) throws Exception {
        log.info("=== NOTIFICATION SERVICE STARTUP HEALTH CHECK ===");
        
        // Run network diagnostics
        connectionHealthService.logNetworkDiagnostics();
        
        // Check Gmail connectivity
        boolean gmailConnected = connectionHealthService.checkGmailConnection();
        
        if (!gmailConnected) {
            log.warn("WARNING: Gmail SMTP not reachable at startup!");
            log.warn("Please check:");
            log.warn("1. Internet connection");
            log.warn("2. Firewall/antivirus settings"); 
            log.warn("3. Corporate network restrictions");
            log.warn("4. App password validity");
        } else {
            log.info(" Gmail SMTP connectivity verified");
        }
        
        log.info("=== STARTUP HEALTH CHECK COMPLETE ===");
    }
} 