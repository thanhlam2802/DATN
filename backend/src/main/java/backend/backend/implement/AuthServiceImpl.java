package backend.backend.implement;

import backend.backend.dto.auth.*;
import backend.backend.entity.Role;
import backend.backend.entity.User;
import backend.backend.entity.UserRole;
import backend.backend.entity.UserRoleId;
import backend.backend.exception.AuthException;
import backend.backend.exception.BadRequestException;
import backend.backend.exception.ErrorCode;
import backend.backend.mapper.UserMapper;
import backend.backend.repository.RoleRepository;
import backend.backend.repository.UserRepository;
import backend.backend.repository.UserRoleRepository;
import backend.backend.service.AuthService;
import backend.backend.service.EmailService;
import backend.backend.service.OTPTransactionService;
import backend.backend.utils.JwtTokenUtil;
import backend.backend.utils.RegexUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service("authService")
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;
    private final HttpServletRequest httpServletRequest;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final OTPTransactionService otpTransactionService;

    @Override
    @Transactional
    public JwtResultDto register(RegisterRequestDto registerRequestDto) {
        Optional<User> user = userRepository.findByEmail(registerRequestDto.getEmail());
        if (user.isPresent()) {
            throw new BadRequestException("User already exist with give email", ErrorCode.AUTH_001);
        }
        if (!UserRoleEnum.registrableUserRoles().contains(registerRequestDto.getRole())) {
            throw new BadRequestException("Invalid role", ErrorCode.AUTH_005);
        }
        User newUser = userMapper.fromRegisterRequestToEntity(registerRequestDto);
        newUser.setPasswordHash(passwordEncoder.encode(registerRequestDto.getPassword()));
        newUser.setCreatedAt(LocalDateTime.now());
        newUser.setUpdatedAt(LocalDateTime.now());
        newUser = userRepository.save(newUser);

        Optional<Role> role = roleRepository.findByName(registerRequestDto.getRole());

        if (role.isEmpty()) {
            throw new BadRequestException("Invalid role", ErrorCode.AUTH_005);
        }

//        UserRole userRole = new UserRole();
//        userRole.setUser(newUser);
//        userRole.setRole(role.get());
//        userRole.setId(new UserRoleId(Long.valueOf(newUser.getId()), role.get().getId()));
//        userRoleRepository.save(userRole);

        Map<String, String> params = new HashMap<>();
        params.put("toEmail", newUser.getEmail());
        params.put("userId", newUser.getId().toString());
        otpTransactionService.sendOtp(params, OtpType.REGISTER_ACCOUNT);

        JwtResultDto jwtResultDto = new JwtResultDto();
        jwtResultDto.setAccessToken(jwtTokenUtil.generateToken(newUser));
        return jwtResultDto;
    }

    @Override
    public void isAuthenticated() {
        String header = httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null) {
            throw new AuthException("Unauthorized", ErrorCode.AUTH_003);
        }
        if (!header.startsWith("Bearer ")) {
            throw new AuthException("Unauthorized", ErrorCode.AUTH_003);
        }

        String token = header.substring(7);

        jwtTokenUtil.extractUserEmail(token);
    }

    @Override
    public JwtResultDto login(LoginRequestDto loginRequestDto) {
        User user;
        if (RegexUtil.isMatch(RegexUtil.EMAIL_PATTERN, loginRequestDto.getIdentifier())) {
            user = getUserByEmail(loginRequestDto.getIdentifier());
        } else {
            user = getUserByPhone(loginRequestDto.getIdentifier());
        }

        String password = loginRequestDto.getPassword();
        String storedPassword = user.getPasswordHash();
        if (!passwordEncoder.matches(password, storedPassword)) {
            throw new BadRequestException("Wrong password", ErrorCode.AUTH_004);
        }
        JwtResultDto jwtResultDto = new JwtResultDto();
        jwtResultDto.setAccessToken(jwtTokenUtil.generateToken(user));
        return jwtResultDto;
    }

    @Override
    public User getUserByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            throw new BadRequestException("User not found", ErrorCode.AUTH_002);
        }
        return user.get();
    }

    @Override
    public User getUserByPhone(String phone) {
        Optional<User> user = userRepository.findByPhone(phone);
        if (user.isEmpty()) {
            throw new BadRequestException("User not found", ErrorCode.AUTH_002);
        }
        return user.get();
    }

}
