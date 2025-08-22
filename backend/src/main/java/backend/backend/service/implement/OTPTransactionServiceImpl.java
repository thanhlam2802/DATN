package backend.backend.service.implement;

import backend.backend.dto.auth.JwtResultDto;
import backend.backend.dto.auth.OtpType;
import backend.backend.dto.email.SendEmailRequestDto;
import backend.backend.entity.OTPTransaction;
import backend.backend.entity.User;
import backend.backend.exception.BadRequestException;
import backend.backend.exception.ErrorCode;
import backend.backend.repository.OTPTransactionRepository;
import backend.backend.repository.UserRepository;
import backend.backend.service.EmailService;
import backend.backend.service.OTPTransactionService;
import backend.backend.utils.JwtTokenUtil;
import backend.backend.utils.TemplateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OTPTransactionServiceImpl implements OTPTransactionService {
    private final EmailService emailService;
    private final OTPTransactionRepository otpTransactionRepository;
    private final UserRepository userRepository;
    private final JwtTokenUtil jwtTokenUtil;
    private final Long expiredInMinutes = 5L;

    @Override
    @Transactional
    public void sendOtp(Map<String, String> params, OtpType type) {
        Integer userId = Integer.parseInt(params.get("userId"));
        switch (type) {
            case VERIFY_ACCOUNT -> {
                OTPTransaction otpTransaction = new OTPTransaction();
                String otpCode = generateOTP();
                otpTransaction.setOtpCode(otpCode);
                otpTransaction.setUserId(userId);
                otpTransaction.setCreatedAt(LocalDateTime.now());
                otpTransaction.setExpiredInMinute(expiredInMinutes);
                otpTransaction.setType(type);

                otpTransactionRepository.save(otpTransaction);
                params.put("OTP_CODE", otpCode);
                params.put("EXPIRED_MINUTE", String.valueOf(expiredInMinutes));
                params.put("USERNAME", params.get("userName"));
                SendEmailRequestDto requestDto = new SendEmailRequestDto();
                requestDto.setTo(params.get("toEmail"));
                requestDto.setSubject("Verify OTP Travela");
                requestDto.setBody(TemplateUtil.process("templates/register_otp_verify.html", params));
                emailService.sendEmail(requestDto);
            }
        }
    }

    @Override
    public void verifyOtp(Integer userId, OtpType type, String code) {
        Optional<OTPTransaction> otpTransaction = otpTransactionRepository
                .findFirstByUserIdAndTypeOrderByCreatedAtDesc(userId, type);
        if (otpTransaction.isEmpty()) {
            throw new BadRequestException("Cannot find otp transaction", ErrorCode.OTP_001);
        }

        if (!otpTransaction.get().getOtpCode().equals(code)) {
            throw new BadRequestException("Invalid otp code", ErrorCode.OTP_002);
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime createdAt = otpTransaction.get().getCreatedAt();
        if (now.isAfter(createdAt.plusMinutes(otpTransaction.get().getExpiredInMinute()))) {
            throw new BadRequestException("Expired OTP transaction", ErrorCode.OTP_003);
        }
    }


    @Override
    public void verifyAcquiredOtp(Integer userId, OtpType type, String code) {
        Optional<OTPTransaction> otpTransaction = otpTransactionRepository
                .findFirstByUserIdAndTypeOrderByCreatedAtDesc(userId, type);
        if (otpTransaction.isEmpty()) {
            throw new BadRequestException("Cannot find otp transaction", ErrorCode.OTP_001);
        }
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime createdAt = otpTransaction.get().getCreatedAt();
        if (now.isAfter(createdAt.plusMinutes(otpTransaction.get().getExpiredInMinute()))) {
            throw new BadRequestException("Expired OTP transaction", ErrorCode.OTP_003);
        }
    }

    @Override
    public OTPTransaction acquireOtp(Integer userId, OtpType type, Long expiredInMinutes) {
        OTPTransaction otpTransaction = new OTPTransaction();
        String otpCode = generateOTP();
        otpTransaction.setOtpCode(otpCode);
        otpTransaction.setUserId(userId);
        otpTransaction.setCreatedAt(LocalDateTime.now());
        otpTransaction.setExpiredInMinute(expiredInMinutes);
        otpTransaction.setType(type);
        otpTransactionRepository.save(otpTransaction);
        return otpTransaction;
    }

    public String generateOTP() {
        SecureRandom random = new SecureRandom();
        int otp = 100_000 + random.nextInt(900_000); // ensures 6 digits (100000–999999)
        return String.valueOf(otp);
    }

}