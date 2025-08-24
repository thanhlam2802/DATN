package backend.backend.service;

import backend.backend.dto.AccountDto;
import backend.backend.dto.auth.JwtResultDto;
import backend.backend.dto.auth.ChangePasswordRequestDto;

public interface AccountService {
    AccountDto getAccountDetails(String username);
    AccountDto updateAccount(AccountDto dto);
    JwtResultDto changePassword(ChangePasswordRequestDto changePasswordRequestDto);
}
