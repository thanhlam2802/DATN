package com.example.Notification_service.dto;

public class MailBookingDto {
    private String recipient;
    private String recipientName;
    private String subject;
    private String content;
    // getter, setter, constructor
    public MailBookingDto() {}
    public MailBookingDto(String recipient, String recipientName, String subject, String content) {
        this.recipient = recipient;
        this.recipientName = recipientName;
        this.subject = subject;
        this.content = content;
    }
    public String getRecipient() { return recipient; }
    public void setRecipient(String recipient) { this.recipient = recipient; }
    public String getRecipientName() { return recipientName; }
    public void setRecipientName(String recipientName) { this.recipientName = recipientName; }
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
} 