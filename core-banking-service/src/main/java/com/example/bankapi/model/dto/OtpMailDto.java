package com.example.bankapi.model.dto;

public class OtpMailDto {
    private String recipient;
    private String recipientName;
    private String bankName;
    private String otp;
    private String transactionId;
    private String amount;
    private String time;
    private String expiredTime;
    public OtpMailDto() {}
    public OtpMailDto(String recipient, String recipientName, String bankName, String otp, String transactionId, String amount, String time, String expiredTime) {
        this.recipient = recipient;
        this.recipientName = recipientName;
        this.bankName = bankName;
        this.otp = otp;
        this.transactionId = transactionId;
        this.amount = amount;
        this.time = time;
        this.expiredTime = expiredTime;
    }

    public OtpMailDto(String recipient, String recipientName, String otp, String xácNhậnHủyVé, String s, String now) {
    }

    public String getRecipient() { return recipient; }
    public void setRecipient(String recipient) { this.recipient = recipient; }
    public String getRecipientName() { return recipientName; }
    public void setRecipientName(String recipientName) { this.recipientName = recipientName; }
    public String getBankName() { return bankName; }
    public void setBankName(String bankName) { this.bankName = bankName; }
    public String getOtp() { return otp; }
    public void setOtp(String otp) { this.otp = otp; }
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    public String getAmount() { return amount; }
    public void setAmount(String amount) { this.amount = amount; }
    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
    public String getExpiredTime() { return expiredTime; }
    public void setExpiredTime(String expiredTime) { this.expiredTime = expiredTime; }
} 