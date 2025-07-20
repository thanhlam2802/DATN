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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);

    @Autowired
    public PaymentServiceImpl(PaymentRepository paymentRepository, RefundRepository refundRepository, AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.paymentRepository = paymentRepository;
        this.refundRepository = refundRepository;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    public Payment initiatePayment(Long debtorAccountId, Long creditorAccountId, BigDecimal amount, String currency, String remittanceInfo, String idempotencyKey) {
        logger.info("[PaymentService] initiatePayment - debtorAccountId: {}, creditorAccountId: {}, amount: {}, currency: {}, remittanceInfo: {}, idempotencyKey: {}", debtorAccountId, creditorAccountId, amount, currency, remittanceInfo, idempotencyKey);
        Account debtor = accountRepository.findById(debtorAccountId)
                .orElseThrow(() -> new AccountNotFoundException("Không tìm thấy tài khoản ghi nợ"));
        Account creditor = accountRepository.findById(creditorAccountId)
                .orElseThrow(() -> new AccountNotFoundException("Không tìm thấy tài khoản ghi có"));
        logger.info("[PaymentService] initiatePayment - Debtor: id={}, accountNumber={}, balance={}", debtor.getId(), debtor.getAccountNumber(), debtor.getAvailableBalance());
        logger.info("[PaymentService] initiatePayment - Creditor: id={}, accountNumber={}, balance={}", creditor.getId(), creditor.getAccountNumber(), creditor.getAvailableBalance());
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
        logger.info("[PaymentService] initiatePayment - Payment created: id={}, paymentId={}, amount={}, status={}, debtorAccountId={}, creditorAccountId={}, createdAt={}",
            payment.getId(), payment.getPaymentId(), payment.getAmount(), payment.getStatus(), payment.getDebtorAccount().getId(), payment.getCreditorAccount().getId(), payment.getCreatedAt()
        );
        // Tạo transaction cho tài khoản ghi nợ
        Transaction t1 = new Transaction();
        t1.setAccount(debtor);
        t1.setBookingDate(java.time.LocalDate.now());
        t1.setAmount(amount.negate());
        t1.setDescription("Chuyển tiền tới " + creditor.getAccountNumber());
        transactionRepository.save(t1);
        logger.info("[PaymentService] initiatePayment - Transaction created: id={}, accountId={}, amount={}, description={}, bookingDate={}",
            t1.getId(), t1.getAccount().getId(), t1.getAmount(), t1.getDescription(), t1.getBookingDate()
        );
        // Tạo transaction cho tài khoản ghi có
        Transaction t2 = new Transaction();
        t2.setAccount(creditor);
        t2.setBookingDate(java.time.LocalDate.now());
        t2.setAmount(amount);
        t2.setDescription("Nhận tiền từ " + debtor.getAccountNumber());
        transactionRepository.save(t2);
        logger.info("[PaymentService] initiatePayment - Transaction created: id={}, accountId={}, amount={}, description={}, bookingDate={}",
            t2.getId(), t2.getAccount().getId(), t2.getAmount(), t2.getDescription(), t2.getBookingDate()
        );
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
    public Payment payServicePayment(Long customerAccountId, BigDecimal amount, String currency, String remittanceInfo, String idempotencyKey) {
        Account customer = accountRepository.findById(customerAccountId)
                .orElseThrow(() -> new AccountNotFoundException("Không tìm thấy tài khoản khách hàng"));
        Account system = accountRepository.findById(FIXED_SOURCE_ACCOUNT_ID)
                .orElseThrow(() -> new AccountNotFoundException("Không tìm thấy tài khoản nguồn hệ thống"));
        logger.info("[PaymentService] payServicePayment - Customer: id={}, accountNumber={}, balance={}", customer.getId(), customer.getAccountNumber(), customer.getAvailableBalance());
        logger.info("[PaymentService] payServicePayment - System: id={}, accountNumber={}, balance={}", system.getId(), system.getAccountNumber(), system.getAvailableBalance());
        if (customer.getAvailableBalance().add(customer.getOverdraftLimit()).compareTo(amount) < 0) {
            throw new InsufficientFundsException("Số dư tài khoản khách hàng không đủ");
        }
        // Trừ tiền khách hàng
        customer.setAvailableBalance(customer.getAvailableBalance().subtract(amount));
        customer.setCurrentBalance(customer.getCurrentBalance().subtract(amount));
        // Cộng tiền hệ thống
        system.setAvailableBalance(system.getAvailableBalance().add(amount));
        system.setCurrentBalance(system.getCurrentBalance().add(amount));
        accountRepository.save(customer);
        accountRepository.save(system);
        // Lưu payment
        Payment payment = new Payment();
        payment.setDebtorAccount(customer);
        payment.setCreditorAccount(system);
        payment.setAmount(amount);
        payment.setCurrency(currency);
        payment.setStatus("COMPLETED");
        payment.setRemittanceInfo(remittanceInfo);
        payment.setIdempotencyKey(idempotencyKey);
        payment = paymentRepository.save(payment);
        logger.info("[PaymentService] payServicePayment - Payment created: id={}, paymentId={}, amount={}, status={}, debtorAccountId={}, creditorAccountId={}, createdAt={}",
            payment.getId(), payment.getPaymentId(), payment.getAmount(), payment.getStatus(), payment.getDebtorAccount().getId(), payment.getCreditorAccount().getId(), payment.getCreatedAt()
        );
        // Tạo transaction khách hàng
        Transaction t1 = new Transaction();
        t1.setAccount(customer);
        t1.setBookingDate(java.time.LocalDate.now());
        t1.setAmount(amount.negate());
        t1.setDescription("Thanh toán dịch vụ vào hệ thống");
        transactionRepository.save(t1);
        logger.info("[PaymentService] payServicePayment - Transaction created: id={}, accountId={}, amount={}, description={}, bookingDate={}",
            t1.getId(), t1.getAccount().getId(), t1.getAmount(), t1.getDescription(), t1.getBookingDate()
        );
        // Tạo transaction hệ thống
        Transaction t2 = new Transaction();
        t2.setAccount(system);
        t2.setBookingDate(java.time.LocalDate.now());
        t2.setAmount(amount);
        t2.setDescription("Nhận thanh toán dịch vụ từ khách hàng " + customer.getAccountNumber());
        transactionRepository.save(t2);
        logger.info("[PaymentService] payServicePayment - Transaction created: id={}, accountId={}, amount={}, description={}, bookingDate={}",
            t2.getId(), t2.getAccount().getId(), t2.getAmount(), t2.getDescription(), t2.getBookingDate()
        );
        return payment;
    }

    @Override
    public Refund refundByTransactionId(UUID transactionId, String reason) {
        logger.info("[PaymentService] refundByTransactionId - transactionId: {}, reason: {}", transactionId, reason);
        Transaction tx = transactionRepository.findByTransactionId(transactionId);
        Payment payment = paymentRepository.findAll().stream()
                .filter(p -> p.getDebtorAccount().getId().equals(tx.getAccount().getId()) || p.getCreditorAccount().getId().equals(tx.getAccount().getId()))
                .findFirst().orElseThrow(() -> new AccountNotFoundException("Không tìm thấy payment liên quan"));
        BigDecimal amount = tx.getAmount().abs();
        Refund refund = refundPayment(payment.getPaymentId(), amount, reason);
        logger.info("[PaymentService] refundByTransactionId - Refund created: id={}, refundId={}, amount={}, status={}, paymentId={}, createdAt={}",
            refund.getId(), refund.getRefundId(), refund.getAmount(), refund.getStatus(), refund.getPayment().getId(), refund.getCreatedAt()
        );
        return refund;
    }
} 