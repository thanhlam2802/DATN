package backend.backend.service;

import backend.backend.dto.auth.OtpType;
import backend.backend.entity.OTPTransaction;

import java.util.Map;

public interface OTPTransactionService {
    void sendOtp(Map<String, String> params, OtpType type);

    void verifyOtp(Integer userId, OtpType type, String code);

    OTPTransaction acquireOtp(Integer userId, OtpType type, Long expiredTime);

    void verifyAcquiredOtp(Integer userId, OtpType type, String code);
}
