package backend.backend.service;

import backend.backend.dto.AccountDto;

public interface AccountService {
    AccountDto getAccountDetails(String email);
    void updateAccount(String email, AccountDto dto);
    void changePassword(String email, String oldPassword, String newPassword);
}
