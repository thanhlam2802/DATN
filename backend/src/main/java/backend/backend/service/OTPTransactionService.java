package backend.backend.service;

import backend.backend.dto.auth.OtpType;

import java.util.Map;

public interface OTPTransactionService {
    void sendOtp(Map<String, String> params, OtpType type);

    void verifyOtp(Long userId, OtpType type, String code);
}
