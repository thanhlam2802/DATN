package backend.backend.service;

import backend.backend.dto.auth.JwtResultDto;
import backend.backend.dto.auth.LoginRequestDto;
import backend.backend.dto.auth.RegisterRequestDto;
import backend.backend.dto.auth.UpdatePasswordRequestDto;
import backend.backend.entity.User;

public interface AuthService {
    JwtResultDto register(RegisterRequestDto registerRequestDto);
    void isAuthenticated();

    JwtResultDto login(LoginRequestDto loginRequestDto);

    User getUserByEmail(String email);
    User getUserByPhone(String phone);

    void updatePassword(UpdatePasswordRequestDto updatePasswordRequestDto);
}
