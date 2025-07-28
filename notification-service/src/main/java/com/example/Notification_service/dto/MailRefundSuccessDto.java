package com.example.Notification_service.dto;

public class MailRefundSuccessDto {
    private String recipient;
    private String recipientName;
    private String bankName;
    private String refundId;
    private String amount;
    private String time;
    private String content;
    // getter, setter, constructor
    public MailRefundSuccessDto() {}
    public MailRefundSuccessDto(String recipient, String recipientName, String bankName, String refundId, String amount, String time, String content) {
        this.recipient = recipient;
        this.recipientName = recipientName;
        this.bankName = bankName;
        this.refundId = refundId;
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
    public String getRefundId() { return refundId; }
    public void setRefundId(String refundId) { this.refundId = refundId; }
    public String getAmount() { return amount; }
    public void setAmount(String amount) { this.amount = amount; }
    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
} 