package backend.backend.controller;

import backend.backend.dto.auth.*;
import backend.backend.service.AuthService;
import backend.backend.service.OTPTransactionService;
import backend.backend.utils.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {

    private final AuthService authService;
    private final OTPTransactionService otpTransactionService;

    @PostMapping("/register")
    public JwtResultDto register(@Valid @RequestBody RegisterRequestDto requestDto) {
        return authService.register(requestDto);
    }

    @PostMapping("/login")
    public JwtResultDto login(@Valid @RequestBody LoginRequestDto requestDto) {
        return authService.login(requestDto);
    }

    @PreAuthorize("@authService.isAuthenticated()")
    @PostMapping("/register/verify-otp")
    public void verifyOtp(@Valid @RequestBody VerifyOtpRequestDto requestDto) {
        Long userId = SecurityUtil.getUserId();
        otpTransactionService.verifyOtp(userId, OtpType.REGISTER_ACCOUNT, requestDto.getCode());
    }
}
