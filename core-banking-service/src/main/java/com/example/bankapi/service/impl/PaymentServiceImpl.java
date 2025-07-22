package com.example.bankapi.service.impl;

import com.example.bankapi.exception.AccountNotFoundException;
import com.example.bankapi.exception.InsufficientFundsException;
import com.example.bankapi.model.dto.RefundDto;
import com.example.bankapi.model.entity.Account;
import com.example.bankapi.model.entity.Payment;
import com.example.bankapi.model.entity.Refund;
import com.example.bankapi.model.entity.Transaction;
import com.example.bankapi.repository.AccountRepository;
import com.example.bankapi.repository.PaymentRepository;
import com.example.bankapi.repository.RefundRepository;
import com.example.bankapi.repository.TransactionRepository;
import com.example.bankapi.service.PaymentService;
import com.example.bankapi.model.dto.PaymentDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.cloud.stream.function.StreamBridge;
import com.example.bankapi.model.dto.OtpMailDto;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import com.example.bankapi.model.dto.ServicePaymentRequestDto;
import com.example.bankapi.model.dto.TransactionDto;

@Service
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final RefundRepository refundRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Value("${corebanking.accountNumber:66666}")
    private String accountNumber;
    @Value("${corebanking.bankCode:MB Bank}")
    private String bankcore;

    private static final Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);

    @Autowired
    public PaymentServiceImpl(PaymentRepository paymentRepository, RefundRepository refundRepository, AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.paymentRepository = paymentRepository;
        this.refundRepository = refundRepository;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private StreamBridge streamBridge;

    public PaymentDto makeServicePayment(ServicePaymentRequestDto req) {
        // Truy vấn account để lấy email và tên
        Account customer = accountRepository.findByBankCodeAndAccountNumber(req.getCustomerBankCode(), req.getCustomerAccountNumber()).orElse(null);
        if (customer == null) {
            throw new AccountNotFoundException("Không tìm thấy tài khoản khách hàng");
        }
        Account system = accountRepository.findByBankCodeAndAccountNumber(bankcore, accountNumber).orElse(null);
        if (system == null) {
            throw new AccountNotFoundException("Không tìm thấy tài khoản nguồn hệ thống");
        }
        String recipient = "phanhuynhphuckhang12c8@gmail.com";
        String recipientName = customer.getAccountHolderName();
        // 1. Tạo OTP
        String otp = String.format("%06d", new Random().nextInt(1000000));
        logger.info("otp: {}",otp);
        // 2. Tạo paymentId
        String paymentId = UUID.randomUUID().toString();
        Payment payment = new Payment();
        payment.setDebtorAccount(customer);
        payment.setCreditorAccount(system);
        payment.setAmount(req.getAmount());
        payment.setPaymentId(UUID.fromString(paymentId));
        payment.setCurrency(req.getCurrency());
        payment.setStatus("PENDING");
        payment.setRemittanceInfo(req.getRemittanceInfo());
        payment.setIdempotencyKey(req.getIdempotencyKey());
        payment = paymentRepository.save(payment);
        logger.info("[PaymentService] payServicePayment - Payment created: id={}, paymentId={}, amount={}, status={}, debtorAccountId={}, creditorAccountId={}, createdAt={}",
                payment.getId(), payment.getPaymentId(), payment.getAmount(), payment.getStatus(), payment.getDebtorAccount().getId(), payment.getCreditorAccount().getId(), payment.getCreatedAt()
        );
        // 3. Lưu OTP vào Redis (10 phút)
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        ops.set("otp:" + paymentId, otp, Duration.ofMinutes(10));
        // 4. Gửi OTP qua Kafka
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        OtpMailDto otpMail = new OtpMailDto(
            recipient,
            recipientName,
            req.getCustomerBankCode(),
            otp,
            paymentId,
            req.getAmount() + " " + req.getCurrency(),
            now,
            LocalDateTime.now().plusMinutes(10).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        );
        streamBridge.send("otpMail-out-0", otpMail);
        // 5. Trả về PaymentDto với paymentId (chưa thực hiện giao dịch)
        PaymentDto dto = new PaymentDto();
        dto.setPaymentId(UUID.fromString(paymentId));
        dto.setStatus("OTP_SENT");
        dto.setAmount(req.getAmount());
        dto.setCurrency(req.getCurrency());
        return dto;
    }

    public TransactionDto confirmServicePayment(String paymentId, String otp) {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        String redisOtp = ops.get("otp:" + paymentId);
        if (redisOtp == null) {
            throw new RuntimeException("OTP expired or not found");
        }
        if (!redisOtp.equals(otp)) {
            throw new RuntimeException("OTP invalid");
        }
        // Xóa OTP khỏi Redis
        redisTemplate.delete("otp:" + paymentId);
        // Thực hiện giao dịch thật sự
        Payment payment = paymentRepository.findByPaymentId(UUID.fromString(paymentId)).orElse(null);
        if (payment == null) {
            throw new RuntimeException("Không tìm thấy payment để xác nhận");
        }
        if (payment.getStatus() != null && payment.getStatus().equals("COMPLETED")) {
            throw new RuntimeException("Giao dịch đã được xác nhận trước đó");
        }
        Account customer = payment.getDebtorAccount();
        Account system = payment.getCreditorAccount();
        BigDecimal amount = payment.getAmount();
        String currency = payment.getCurrency();
        // Kiểm tra số dư
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
        // Cập nhật trạng thái payment
        payment.setStatus("COMPLETED");
        paymentRepository.save(payment);
        // Lưu transaction khách hàng
        Transaction t1 = new Transaction();
        t1.setAccount(customer);
        t1.setBookingDate(java.time.LocalDate.now());
        t1.setAmount(amount.negate());
        t1.setDescription("Thanh toán dịch vụ vào hệ thống");
        transactionRepository.save(t1);
        // Lưu transaction hệ thống
        Transaction t2 = new Transaction();
        t2.setAccount(system);
        t2.setBookingDate(java.time.LocalDate.now());
        t2.setAmount(amount);
        t2.setDescription("Nhận thanh toán dịch vụ từ khách hàng " + customer.getAccountNumber());
        transactionRepository.save(t2);
        // Trả về TransactionDto của t1
        TransactionDto dto = new TransactionDto();
        dto.setTransactionId(t1.getTransactionId());
        dto.setBookingDate(t1.getBookingDate());
        dto.setAmount(t1.getAmount());
        dto.setDescription(t1.getDescription());
        return dto;
    }

    @Override
    public PaymentDto initiatePayment(String debtorAccountNumber, String debtorBankCode, String creditorAccountNumber, String creditorBankCode, BigDecimal amount, String currency, String remittanceInfo, String idempotencyKey) {
        logger.info("[PaymentService] initiatePayment - debtorAccountNumber: {}, debtorBankCode: {}, creditorAccountNumber: {}, creditorBankCode: {}, amount: {}, currency: {}, remittanceInfo: {}, idempotencyKey: {}", debtorAccountNumber, debtorBankCode, creditorAccountNumber, creditorBankCode, amount, currency, remittanceInfo, idempotencyKey);
        Account debtor = accountRepository.findByBankCodeAndAccountNumber(debtorBankCode, debtorAccountNumber).orElse(null);
        if (debtor == null) {
            throw new AccountNotFoundException("Không tìm thấy tài khoản ghi nợ");
        }
        Account creditor = accountRepository.findByBankCodeAndAccountNumber(creditorBankCode, creditorAccountNumber).orElse(null);
        if (creditor == null) {
            throw new AccountNotFoundException("Không tìm thấy tài khoản ghi có");
        }
        logger.info("[PaymentService] initiatePayment - Debtor: id={}, accountNumber={}, bankCode={}, balance={}", debtor.getId(), debtor.getAccountNumber(), debtor.getBankCode(), debtor.getAvailableBalance());
        logger.info("[PaymentService] initiatePayment - Creditor: id={}, accountNumber={}, bankCode={}, balance={}", creditor.getId(), creditor.getAccountNumber(), creditor.getBankCode(), creditor.getAvailableBalance());
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
        return toPaymentDto(payment);
    }

    @Override
    public PaymentDto getStatus(UUID paymentId) {
        Optional<Payment> optionalPayment = paymentRepository.findByPaymentId(paymentId);
        if (optionalPayment.isPresent()) {
            Payment payment = optionalPayment.get();
            return toPaymentDto(payment);
        }
        return null;
    }

    @Override
    public void cancelPayment(UUID paymentId) {
        Payment payment = paymentRepository.findByPaymentId(paymentId).orElse(null);
        if (payment == null) {
            throw new AccountNotFoundException("Không tìm thấy giao dịch thanh toán");
        }
        payment.setStatus("CANCELLED");
        paymentRepository.save(payment);
    }

    @Override
    public RefundDto refundPayment(UUID paymentId, BigDecimal amount, String reason) {
        Payment payment = paymentRepository.findByPaymentId(paymentId).orElse(null);
        if (payment == null) {
            throw new AccountNotFoundException("Không tìm thấy giao dịch thanh toán");
        }
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
        return toRefundDto(refund);
    }

    @Override
    public PaymentDto payServicePayment(String customerAccountNumber, String customerBankCode, BigDecimal amount, String currency, String remittanceInfo, String idempotencyKey) {
        Account customer = accountRepository.findByBankCodeAndAccountNumber(customerBankCode, customerAccountNumber).orElse(null);
        if (customer == null) {
            throw new AccountNotFoundException("Không tìm thấy tài khoản khách hàng");
        }
        Account system = accountRepository.findByBankCodeAndAccountNumber(bankcore, accountNumber).orElse(null);
        if (system == null) {
            throw new AccountNotFoundException("Không tìm thấy tài khoản nguồn hệ thống");
        }
        logger.info("[PaymentService] payServicePayment - Customer: id={}, accountNumber={}, bankCode={}, balance={}", customer.getId(), customer.getAccountNumber(), customer.getBankCode(), customer.getAvailableBalance());
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
        return toPaymentDto(payment);
    }

    @Override
    public RefundDto refundByTransactionId(UUID transactionId, String reason) {
        logger.info("[PaymentService] refundByTransactionId - transactionId: {}, reason: {}", transactionId, reason);
        Transaction tx = transactionRepository.findByTransactionId(transactionId);
        Optional<Payment> paymentOpt = paymentRepository.findFirstByDebtorAccount_IdOrCreditorAccount_Id(
            tx.getAccount().getId(), tx.getAccount().getId()
        );
        Payment payment = paymentOpt.orElse(null);
        if (payment == null) {
            throw new AccountNotFoundException("Không tìm thấy giao dịch thanh toán");
        }
        BigDecimal amount = tx.getAmount().abs();
        RefundDto refund = refundPayment(payment.getPaymentId(), amount, reason);
        logger.info("[PaymentService] refundByTransactionId - Refund created:  refundId={}, amount={}, status={}, createdAt={}",
             refund.getRefundId(), refund.getAmount(), refund.getStatus(), refund.getCreatedAt()
        );
        return refund;
    }

    public PaymentDto toPaymentDto(Payment entity) {
        if (entity == null) return null;
        PaymentDto dto = new PaymentDto();
        dto.setPaymentId(entity.getPaymentId());
        dto.setStatus(entity.getStatus());
        dto.setAmount(entity.getAmount());
        dto.setCurrency(entity.getCurrency());
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    }
    public RefundDto toRefundDto(Refund entity) {
        if (entity == null) return null;
        RefundDto dto = new RefundDto();
        dto.setRefundId(entity.getRefundId());
        dto.setStatus(entity.getStatus());
        dto.setAmount(entity.getAmount());
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    }
} 