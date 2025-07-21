package com.example.bankapi.service.impl;

import com.example.bankapi.exception.AccountNotFoundException;
import com.example.bankapi.exception.InsufficientFundsException;
import com.example.bankapi.model.entity.Account;
import com.example.bankapi.model.entity.Payment;
import com.example.bankapi.model.entity.Refund;
import com.example.bankapi.model.entity.Transaction;
import com.example.bankapi.repository.AccountRepository;
import com.example.bankapi.repository.PaymentRepository;
import com.example.bankapi.repository.RefundRepository;
import com.example.bankapi.repository.TransactionRepository;
import com.example.bankapi.service.PaymentService;
import com.example.bankapi.mapper.PaymentMapper;
import com.example.bankapi.model.dto.PaymentDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Service
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final RefundRepository refundRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    private static final Long FIXED_SOURCE_ACCOUNT_ID = 1L; // ID tài khoản nguồn cố định

    @Autowired
    public PaymentServiceImpl(PaymentRepository paymentRepository, RefundRepository refundRepository, AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.paymentRepository = paymentRepository;
        this.refundRepository = refundRepository;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    public Payment initiatePayment(Long debtorAccountId, Long creditorAccountId, BigDecimal amount, String currency, String remittanceInfo, String idempotencyKey) {
        Account debtor = accountRepository.findById(debtorAccountId)
                .orElseThrow(() -> new AccountNotFoundException("Không tìm thấy tài khoản ghi nợ"));
        Account creditor = accountRepository.findById(creditorAccountId)
                .orElseThrow(() -> new AccountNotFoundException("Không tìm thấy tài khoản ghi có"));
        if (debtor.getAvailableBalance().add(debtor.getOverdraftLimit()).compareTo(amount) < 0) {
            throw new InsufficientFundsException("Số dư không đủ");
        }
        // Trừ tiền
        debtor.setAvailableBalance(debtor.getAvailableBalance().subtract(amount));
        debtor.setCurrentBalance(debtor.getCurrentBalance().subtract(amount));
        // Cộng tiền
        creditor.setAvailableBalance(creditor.getAvailableBalance().add(amount));
        creditor.setCurrentBalance(creditor.getCurrentBalance().add(amount));
        accountRepository.save(debtor);
        accountRepository.save(creditor);
        // Lưu payment
        Payment payment = new Payment();
        payment.setDebtorAccount(debtor);
        payment.setCreditorAccount(creditor);
        payment.setAmount(amount);
        payment.setCurrency(currency);
        payment.setStatus("COMPLETED");
        payment.setRemittanceInfo(remittanceInfo);
        payment.setIdempotencyKey(idempotencyKey);
        payment = paymentRepository.save(payment);
        // Tạo transaction cho tài khoản ghi nợ
        Transaction t1 = new Transaction();
        t1.setAccount(debtor);
        t1.setBookingDate(java.time.LocalDate.now());
        t1.setAmount(amount.negate());
        t1.setDescription("Chuyển tiền tới " + creditor.getAccountNumber());
        transactionRepository.save(t1);
        // Tạo transaction cho tài khoản ghi có
        Transaction t2 = new Transaction();
        t2.setAccount(creditor);
        t2.setBookingDate(java.time.LocalDate.now());
        t2.setAmount(amount);
        t2.setDescription("Nhận tiền từ " + debtor.getAccountNumber());
        transactionRepository.save(t2);
        return payment;
    }

    @Override
    public Optional<Payment> getStatus(UUID paymentId) {
        // TODO: Implement logic
        return paymentRepository.findByPaymentId(paymentId);
    }

    @Override
    public void cancelPayment(UUID paymentId) {
        Payment payment = paymentRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new AccountNotFoundException("Không tìm thấy giao dịch thanh toán"));
        payment.setStatus("CANCELLED");
        paymentRepository.save(payment);
    }

    @Override
    public Refund refundPayment(UUID paymentId, BigDecimal amount, String reason) {
        Payment payment = paymentRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new AccountNotFoundException("Không tìm thấy giao dịch thanh toán"));
        Account creditor = payment.getCreditorAccount();
        Account debtor = payment.getDebtorAccount();
        if (creditor.getAvailableBalance().add(creditor.getOverdraftLimit()).compareTo(amount) < 0) {
            throw new InsufficientFundsException("Số dư tài khoản hoàn không đủ");
        }
        // Trừ tiền tài khoản ghi có
        creditor.setAvailableBalance(creditor.getAvailableBalance().subtract(amount));
        creditor.setCurrentBalance(creditor.getCurrentBalance().subtract(amount));
        // Cộng tiền tài khoản ghi nợ
        debtor.setAvailableBalance(debtor.getAvailableBalance().add(amount));
        debtor.setCurrentBalance(debtor.getCurrentBalance().add(amount));
        accountRepository.save(creditor);
        accountRepository.save(debtor);
        // Lưu refund
        Refund refund = new Refund();
        refund.setPayment(payment);
        refund.setAmount(amount);
        refund.setReason(reason);
        refund.setStatus("COMPLETED");
        refund = refundRepository.save(refund);
        // Tạo transaction hoàn tiền
        Transaction t1 = new Transaction();
        t1.setAccount(creditor);
        t1.setBookingDate(java.time.LocalDate.now());
        t1.setAmount(amount.negate());
        t1.setDescription("Hoàn tiền cho " + debtor.getAccountNumber());
        transactionRepository.save(t1);
        Transaction t2 = new Transaction();
        t2.setAccount(debtor);
        t2.setBookingDate(java.time.LocalDate.now());
        t2.setAmount(amount);
        t2.setDescription("Nhận hoàn tiền từ " + creditor.getAccountNumber());
        transactionRepository.save(t2);
        return refund;
    }

    @Override
    public Payment payWithFeeAccount(Long feeAccountId, Long targetAccountId, BigDecimal amount, String currency, String remittanceInfo, String idempotencyKey) {
        // Tài khoản nguồn cố định
        Account source = accountRepository.findById(FIXED_SOURCE_ACCOUNT_ID)
                .orElseThrow(() -> new AccountNotFoundException("Không tìm thấy tài khoản nguồn cố định"));
        Account target = accountRepository.findById(targetAccountId)
                .orElseThrow(() -> new AccountNotFoundException("Không tìm thấy tài khoản nhận"));
        Account feeAccount = accountRepository.findById(feeAccountId)
                .orElseThrow(() -> new AccountNotFoundException("Không tìm thấy tài khoản chịu phí"));
        // Giả sử phí là 1% số tiền
        BigDecimal fee = amount.multiply(new BigDecimal("0.01")).setScale(2, BigDecimal.ROUND_HALF_UP);
        if (source.getAvailableBalance().add(source.getOverdraftLimit()).compareTo(amount) < 0) {
            throw new InsufficientFundsException("Số dư tài khoản nguồn không đủ");
        }
        if (feeAccount.getAvailableBalance().add(feeAccount.getOverdraftLimit()).compareTo(fee) < 0) {
            throw new InsufficientFundsException("Số dư tài khoản chịu phí không đủ");
        }
        // Trừ tiền nguồn
        source.setAvailableBalance(source.getAvailableBalance().subtract(amount));
        source.setCurrentBalance(source.getCurrentBalance().subtract(amount));
        // Cộng tiền nhận
        target.setAvailableBalance(target.getAvailableBalance().add(amount));
        target.setCurrentBalance(target.getCurrentBalance().add(amount));
        // Trừ phí
        feeAccount.setAvailableBalance(feeAccount.getAvailableBalance().subtract(fee));
        feeAccount.setCurrentBalance(feeAccount.getCurrentBalance().subtract(fee));
        accountRepository.save(source);
        accountRepository.save(target);
        accountRepository.save(feeAccount);
        // Lưu payment
        Payment payment = new Payment();
        payment.setDebtorAccount(source);
        payment.setCreditorAccount(target);
        payment.setAmount(amount);
        payment.setCurrency(currency);
        payment.setStatus("COMPLETED");
        payment.setRemittanceInfo(remittanceInfo + " (fee: " + fee + ")");
        payment.setIdempotencyKey(idempotencyKey);
        payment = paymentRepository.save(payment);
        // Tạo transaction nguồn
        Transaction t1 = new Transaction();
        t1.setAccount(source);
        t1.setBookingDate(java.time.LocalDate.now());
        t1.setAmount(amount.negate());
        t1.setDescription("Chuyển tiền tới " + target.getAccountNumber());
        transactionRepository.save(t1);
        // Tạo transaction nhận
        Transaction t2 = new Transaction();
        t2.setAccount(target);
        t2.setBookingDate(java.time.LocalDate.now());
        t2.setAmount(amount);
        t2.setDescription("Nhận tiền từ " + source.getAccountNumber());
        transactionRepository.save(t2);
        // Tạo transaction phí
        Transaction t3 = new Transaction();
        t3.setAccount(feeAccount);
        t3.setBookingDate(java.time.LocalDate.now());
        t3.setAmount(fee.negate());
        t3.setDescription("Thanh toán phí chuyển tiền");
        transactionRepository.save(t3);
        return payment;
    }

    @Override
    public Refund refundByTransactionId(Long transactionId, String reason) {
        Transaction tx = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new AccountNotFoundException("Không tìm thấy transaction"));
        Payment payment = paymentRepository.findAll().stream()
                .filter(p -> p.getDebtorAccount().getId().equals(tx.getAccount().getId()) || p.getCreditorAccount().getId().equals(tx.getAccount().getId()))
                .findFirst().orElseThrow(() -> new AccountNotFoundException("Không tìm thấy payment liên quan"));
        BigDecimal amount = tx.getAmount().abs();
        return refundPayment(payment.getPaymentId(), amount, reason);
    }
} 