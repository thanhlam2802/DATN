package backend.backend.implement;

import backend.backend.dto.AccountDto;
import backend.backend.entity.User;
import backend.backend.mapper.UserMapper;
import backend.backend.repository.UserRepository;
import backend.backend.service.AccountService;
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
    public void updateAccount(AccountDto dto) {

    }
}
