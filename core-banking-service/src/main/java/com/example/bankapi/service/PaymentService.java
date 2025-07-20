package com.example.bankapi.service;

import com.example.bankapi.model.entity.Payment;
import com.example.bankapi.model.entity.Refund;
import com.example.bankapi.model.dto.PaymentDto;
import com.example.bankapi.model.dto.RefundDto;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public interface PaymentService {
    PaymentDto initiatePayment(String debtorAccountNumber, String debtorBankCode, String creditorAccountNumber, String creditorBankCode, java.math.BigDecimal amount, String currency, String remittanceInfo, String idempotencyKey);
    PaymentDto payServicePayment(String customerAccountNumber, String customerBankCode, java.math.BigDecimal amount, String currency, String remittanceInfo, String idempotencyKey);
    RefundDto refundPayment(UUID paymentId, java.math.BigDecimal amount, String reason);
    RefundDto refundByTransactionId(UUID transactionId, String reason);
    PaymentDto getStatus(UUID paymentId);
    void cancelPayment(UUID paymentId);
} 