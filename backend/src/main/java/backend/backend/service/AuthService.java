package backend.backend.service;

import backend.backend.dto.auth.*;
import backend.backend.entity.User;

public interface AuthService {
    JwtResultDto register(RegisterRequestDto registerRequestDto);

    void isAuthenticated();

    JwtResultDto login(LoginRequestDto loginRequestDto);

    User getUserByEmail(String email);

    User getUserByPhone(String phone);

    JwtResultDto updatePassword(UpdatePasswordRequestDto updatePasswordRequestDto);

    JwtResultDto resetPassword(ResetPasswordRequestDto resetPasswordRequestDto);

    void resetPasswordVerifyLink(ResetPasswordVerifyLinkDto resetPasswordVerifyLinkDto);

    void requestResetPassword(RequestResetPasswordRequestDto requestDto);

    JwtResultDto verifyAccount(VerifyAccountRequestDto verifyAccountRequestDto);

    void verifyAccountResend(String email);

    JwtResultDto refreshToken(RefreshTokenRequestDto refreshTokenRequestDto);
}
