package backend.backend.service;

import backend.backend.dto.auth.JwtResultDto;
import backend.backend.dto.auth.RegisterRequestDto;

public interface AuthService {
    JwtResultDto register(RegisterRequestDto registerRequestDto);
    void isAuthenticated();
}
