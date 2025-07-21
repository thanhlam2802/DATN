package com.example.Notification_service.dto;

public class MailPaymentSuccessDto {
    private String recipient;
    private String recipientName;
    private String bankName;
    private String transactionId;
    private String amount;
    private String time;
    private String content;
    // getter, setter, constructor
    public MailPaymentSuccessDto() {}
    public MailPaymentSuccessDto(String recipient, String recipientName, String bankName, String transactionId, String amount, String time, String content) {
        this.recipient = recipient;
        this.recipientName = recipientName;
        this.bankName = bankName;
        this.transactionId = transactionId;
        this.amount = amount;
        this.time = time;
        this.content = content;
    }
    public String getRecipient() { return recipient; }
    public void setRecipient(String recipient) { this.recipient = recipient; }
    public String getRecipientName() { return recipientName; }
    public void setRecipientName(String recipientName) { this.recipientName = recipientName; }
    public String getBankName() { return bankName; }
    public void setBankName(String bankName) { this.bankName = bankName; }
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    public String getAmount() { return amount; }
    public void setAmount(String amount) { this.amount = amount; }
    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
} 