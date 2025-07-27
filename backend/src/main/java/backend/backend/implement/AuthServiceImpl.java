package backend.backend.implement;

import backend.backend.dto.auth.*;
import backend.backend.dto.email.SendEmailRequestDto;
import backend.backend.entity.*;
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
import backend.backend.utils.SecurityUtil;
import backend.backend.utils.TemplateUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
    private final EmailService emailService;

    @Value("${app.web.domain}")
    private String webDomain;

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
        newUser.setIsVerified(false);

        newUser = userRepository.save(newUser);

        Optional<Role> role = roleRepository.findByName(registerRequestDto.getRole());

        if (role.isEmpty()) {
            throw new BadRequestException("Invalid role", ErrorCode.AUTH_005);
        }

        UserRole userRole = new UserRole();
        userRole.setUser(newUser);
        userRole.setRole(role.get());
        userRole.setId(new UserRoleId(newUser.getId(), role.get().getId()));
        userRoleRepository.save(userRole);

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

        Claims claims = jwtTokenUtil.extractAllClaims(token);
        boolean verified = claims.get("isVerified", Boolean.class);
        if (!verified) {
            throw new AuthException("Unauthorized", ErrorCode.AUTH_007);
        }
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
        if (!user.getIsVerified()) {
            throw new BadRequestException("User is not verified", ErrorCode.AUTH_007);
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

    @Override
    public JwtResultDto updatePassword(UpdatePasswordRequestDto updatePasswordRequestDto) {
        String email = SecurityUtil.getCurrentUserEmail();
        User user = getUserByEmail(email);
        String oldPassword = updatePasswordRequestDto.getOldPassword();
        if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
            throw new BadRequestException("Password is not match", ErrorCode.AUTH_005);
        }
        user.setPasswordHash(passwordEncoder.encode(updatePasswordRequestDto.getNewPassword()));
        user = userRepository.save(user);
        JwtResultDto jwtResultDto = new JwtResultDto();
        jwtResultDto.setAccessToken(jwtTokenUtil.generateToken(user));
        return jwtResultDto;
    }

    @Override
    public JwtResultDto resetPassword(ResetPasswordRequestDto resetPasswordRequestDto) {
        Integer userId = jwtTokenUtil.extractUserId(resetPasswordRequestDto.getResetToken());

        Optional<User> user = userRepository.findById(userId);

        if (user.isEmpty()) {
            throw new BadRequestException("User not found", ErrorCode.AUTH_002);
        }
        ResetPasswordVerifyLinkDto resetPasswordVerifyLinkDto = new ResetPasswordVerifyLinkDto();
        resetPasswordVerifyLinkDto.setResetToken(resetPasswordRequestDto.getResetToken());
        resetPasswordVerifyLinkDto.setOtpCode(resetPasswordRequestDto.getOtpCode());
        resetPasswordVerifyLink(resetPasswordVerifyLinkDto);
        User userUpdated = user.get();
        userUpdated.setPasswordHash(passwordEncoder.encode(resetPasswordRequestDto.getNewPassword()));
        userUpdated = userRepository.save(userUpdated);
        JwtResultDto jwtResultDto = new JwtResultDto();
        jwtResultDto.setAccessToken(jwtTokenUtil.generateToken(userUpdated));
        return jwtResultDto;
    }

    @Override
    public void resetPasswordVerifyLink(ResetPasswordVerifyLinkDto resetPasswordVerifyLinkDto) {
        Integer userId = jwtTokenUtil.extractUserId(resetPasswordVerifyLinkDto.getResetToken());
        otpTransactionService.verifyAcquiredOtp(userId, OtpType.RESET_PASSWORD, resetPasswordVerifyLinkDto.getOtpCode());
    }

    @Override
    @Transactional
    public void requestResetPassword(RequestResetPasswordRequestDto requestDto) {
        User user = getUserByEmail(requestDto.getEmail());

        SendEmailRequestDto emailRequestDto = new SendEmailRequestDto();
        emailRequestDto.setTo(requestDto.getEmail());
        emailRequestDto.setSubject("Reset Password");
        String token = jwtTokenUtil.generateToken(user);
        OTPTransaction otpTransaction = otpTransactionService.acquireOtp(user.getId(), OtpType.RESET_PASSWORD, 30L);
        String resetLink = webDomain.concat("/reset-password").concat("?rt=").concat(token).concat("&otp=").concat(otpTransaction.getOtpCode());
        Map<String, String> params = new HashMap<>();
        params.put("RESET_LINK", resetLink);
        params.put("USERNAME", user.getName());
        params.put("EXPIRED_MINUTES", String.valueOf(30L));

        emailRequestDto.setBody(TemplateUtil.process("templates/reset_password.html", params));
        emailService.sendEmail(emailRequestDto);
    }

}
