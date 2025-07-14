package backend.backend.service;

import backend.backend.dto.AccountDto;

public interface AccountService {
    AccountDto getAccountDetails(String username);
    AccountDto updateAccount(AccountDto dto);
}
