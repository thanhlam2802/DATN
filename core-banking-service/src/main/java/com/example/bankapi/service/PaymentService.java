package com.example.bankapi.service;

import com.example.bankapi.model.entity.Payment;
import com.example.bankapi.model.entity.Refund;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public interface PaymentService {
    Payment initiatePayment(Long debtorAccountId, Long creditorAccountId, BigDecimal amount, String currency, String remittanceInfo, String idempotencyKey);
    Optional<Payment> getStatus(UUID paymentId);
    void cancelPayment(UUID paymentId);
    Refund refundPayment(UUID paymentId, BigDecimal amount, String reason);
    Payment payWithFeeAccount(Long feeAccountId, Long targetAccountId, java.math.BigDecimal amount, String currency, String remittanceInfo, String idempotencyKey);
    Refund refundByTransactionId(Long transactionId, String reason);
} 