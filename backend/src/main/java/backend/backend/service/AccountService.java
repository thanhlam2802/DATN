package backend.backend.service;

import backend.backend.dto.AccountDto;

public interface AccountService {
    AccountDto getAccountDetails(String username);
    void updateAccount(AccountDto dto);
}
