package backend.backend.controller;

import backend.backend.dto.auth.*;
import backend.backend.repository.UserRepository;
import backend.backend.service.AuthService;
import backend.backend.service.OTPTransactionService;
import backend.backend.utils.SecurityUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public JwtResultDto register(@Valid @RequestBody RegisterRequestDto requestDto) {
        return authService.register(requestDto);
    }

    @PostMapping("/login")
    public JwtResultDto login(@Valid @RequestBody LoginRequestDto requestDto) {
        return authService.login(requestDto);
    }


    @PostMapping("/verify-account")
    public JwtResultDto verifyAccount(@Valid @RequestBody VerifyAccountRequestDto requestDto) {
        return authService.verifyAccount(requestDto);
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

    @PostMapping("/reset-password/verify-link")
    public void resetPasswordVerifyLink(@Valid @RequestBody ResetPasswordVerifyLinkDto requestDto) {
        authService.resetPasswordVerifyLink(requestDto);
    }

    @PostMapping("/verify-account/resend")
    public void verifyAccountResend(@Valid @Email @RequestBody String email) {
        authService.verifyAccountResend(email);
    }

    @PostMapping("/refresh-token")
    public JwtResultDto refreshToken(@Valid @RequestBody RefreshTokenRequestDto requestDto) {
        return authService.refreshToken(requestDto);
    }
}
