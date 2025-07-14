package backend.backend.implement;

import backend.backend.dto.AccountDto;
import backend.backend.entity.User;
import backend.backend.exception.BadRequestException;
import backend.backend.exception.ErrorCode;
import backend.backend.mapper.UserMapper;
import backend.backend.repository.UserRepository;
import backend.backend.service.AccountService;
import backend.backend.utils.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public AccountDto getAccountDetails(String userEmail) {
        Optional<User> user = userRepository.findByEmail(userEmail);
        return user.map(userMapper::fromEntityToProfile).orElse(null);
    }

    @Override
    public AccountDto updateAccount(AccountDto dto) {
        String email = SecurityUtil.getCurrentUserEmail();
        Optional<User> user = userRepository.findByEmail(email);
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
}
