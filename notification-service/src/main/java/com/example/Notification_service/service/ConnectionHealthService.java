package com.example.Notification_service.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

@Slf4j
@Service
public class ConnectionHealthService {

    public boolean checkGmailConnection() {
        return checkSMTPConnection("smtp.gmail.com", 587);
    }

    public boolean checkSMTPConnection(String host, int port) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), 10000); // 10 second timeout
            log.info("Successfully connected to {}:{}", host, port);
            return true;
        } catch (SocketTimeoutException e) {
            log.error("Timeout connecting to {}:{} - {}", host, port, e.getMessage());
            return false;
        } catch (IOException e) {
            log.error("Failed to connect to {}:{} - {}", host, port, e.getMessage());
            return false;
        }
    }

    public void logNetworkDiagnostics() {
        log.info("=== NETWORK DIAGNOSTICS ===");
        log.info("Java version: {}", System.getProperty("java.version"));
        log.info("OS: {}", System.getProperty("os.name"));
        log.info("IPv4 preferred: {}", System.getProperty("java.net.preferIPv4Stack"));
        log.info("DNS servers: {}", System.getProperty("sun.net.spi.nameservice.nameservers"));
        
        // Test Gmail SMTP connection
        boolean gmailReachable = checkGmailConnection();
        log.info("Gmail SMTP (587) reachable: {}", gmailReachable);
        
        // Test alternative Gmail SMTP SSL
        boolean gmailSSLReachable = checkSMTPConnection("smtp.gmail.com", 465);
        log.info("Gmail SMTPS (465) reachable: {}", gmailSSLReachable);
        
        log.info("=== END DIAGNOSTICS ===");
    }
} 