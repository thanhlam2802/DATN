package backend.backend.service;

import backend.backend.dto.auth.*;
import backend.backend.entity.User;
import backend.backend.utils.JwtTokenUtil;

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
}
