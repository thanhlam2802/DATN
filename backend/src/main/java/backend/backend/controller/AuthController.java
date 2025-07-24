package backend.backend.controller;

import backend.backend.dto.auth.*;
import backend.backend.entity.User;
import backend.backend.repository.UserRepository;
import backend.backend.service.AuthService;
import backend.backend.service.OTPTransactionService;
import backend.backend.utils.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {

    private final AuthService authService;
    private final OTPTransactionService otpTransactionService;
    private final UserRepository userRepository;

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
    public JwtResultDto verifyOtp(@Valid @RequestBody VerifyOtpRequestDto requestDto) {
        Integer userId = SecurityUtil.getUserId();
        return otpTransactionService.verifyOtp(userId, OtpType.REGISTER_ACCOUNT, requestDto.getCode());
    }


    @PreAuthorize("@authService.isAuthenticated()")
    @PostMapping("/update-password")
    public JwtResultDto update(@Valid @RequestBody UpdatePasswordRequestDto requestDto) {
        return authService.updatePassword(requestDto);
    }

    @PostMapping("/forgot-password/request")
    public void requestResetPassword(@Valid @RequestBody RequestResetPasswordRequestDto requestDto) {
        authService.requestResetPassword(requestDto);
    }

    @PostMapping("/forgot-password/reset")
    public JwtResultDto resetPassword(@Valid @RequestBody ResetPasswordRequestDto requestDto) {
        return authService.resetPassword(requestDto);
    }
}
