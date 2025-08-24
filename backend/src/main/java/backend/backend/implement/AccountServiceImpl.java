package backend.backend.implement;

import backend.backend.dto.AccountDto;
import backend.backend.dto.auth.ChangePasswordRequestDto;
import backend.backend.dto.auth.JwtResultDto;
import backend.backend.entity.User;
import backend.backend.exception.BadRequestException;
import backend.backend.exception.ErrorCode;
import backend.backend.mapper.UserMapper;
import backend.backend.repository.UserRepository;
import backend.backend.service.AccountService;
import backend.backend.utils.JwtTokenUtil;
import backend.backend.utils.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service("accountService")
@Slf4j
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;

    @Override
    @Transactional(readOnly = true)
    public AccountDto getAccountDetails(String userEmail) {
        System.out.println("🔍 Đang tìm user với email: " + userEmail);
        Optional<User> user = userRepository.findByEmailWithRoles(userEmail);
        if (user.isEmpty()) {
            System.out.println("❌ Không tìm thấy user với email: " + userEmail);
        }
        return user.map(userMapper::fromEntityToProfile).orElse(null);
    }

    @Override
    @Transactional
    public AccountDto updateAccount(AccountDto dto) {
        String email = SecurityUtil.getCurrentUserEmail();
        Optional<User> user = userRepository.findByEmailWithRoles(email);
        if (user.isPresent()) {
            User userEntity = user.get();
            userEntity.setName(dto.getName());
            userEntity.setGender(dto.getGender());
            userEntity.setBirthday(dto.getBirthday());
            userRepository.save(userEntity);
            return userMapper.fromEntityToProfile(userEntity);
        } else {
            throw new BadRequestException("User not found", ErrorCode.AUTH_002);
        }
    }

    @Override
    @Transactional
    public JwtResultDto changePassword(ChangePasswordRequestDto changePasswordRequestDto) {
        String email = SecurityUtil.getCurrentUserEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("User not found", ErrorCode.AUTH_002));

        String oldPassword = changePasswordRequestDto.getOldPassword();
        if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
            throw new BadRequestException("Password is not match", ErrorCode.AUTH_005);
        }

        user.setPasswordHash(passwordEncoder.encode(changePasswordRequestDto.getNewPassword()));
        user = userRepository.save(user);

        JwtResultDto jwtResultDto = new JwtResultDto();
        jwtResultDto.setAccessToken(jwtTokenUtil.generateToken(user));
        jwtResultDto.setRefreshToken(jwtTokenUtil.generateRefreshToken(user));
        return jwtResultDto;
    }
}
