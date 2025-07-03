package backend.backend.implement;

import backend.backend.dto.auth.JwtResultDto;
import backend.backend.dto.auth.RegisterRequestDto;
import backend.backend.entity.User;
import backend.backend.exception.BadRequestException;
import backend.backend.exception.ErrorCode;
import backend.backend.repository.UserRepository;
import backend.backend.service.AuthService;
import backend.backend.utils.JwtTokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service("authService")
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;
    private final HttpServletRequest httpServletRequest;

    @Override
    public JwtResultDto register(RegisterRequestDto registerRequestDto) {
        Optional<User> user = userRepository.findByEmail(registerRequestDto.getEmail());
        if (user.isPresent()) {
            throw new BadRequestException("User already exist with give email", ErrorCode.AUTH_001);
        }
        User newUser = new User();
        newUser.setEmail(registerRequestDto.getEmail());
        newUser.setName(registerRequestDto.getFullName());
        newUser.setPasswordHash(passwordEncoder.encode(registerRequestDto.getPassword()));

        newUser = userRepository.save(newUser);
        JwtResultDto jwtResultDto = new JwtResultDto();
        jwtResultDto.setAccessToken(jwtTokenUtil.generateToken(newUser));
        return jwtResultDto;
    }

    @Override
    public void isAuthenticated() {
        String header = httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            String userEmail = jwtTokenUtil.extractUserEmail(token);

            Optional<User> user = userRepository.findByEmail(userEmail);
            if (user.isEmpty()) {
                throw new BadRequestException("User not found", ErrorCode.AUTH_002);
            }
        }
    }
}
