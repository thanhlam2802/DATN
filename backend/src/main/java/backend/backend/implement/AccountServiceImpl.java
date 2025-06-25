package backend.backend.implement;

import backend.backend.dto.AccountDto;
import backend.backend.service.AccountService;

public class AccountServiceImpl implements AccountService {
    @Override
    public AccountDto getAccountDetails(String email) {
        return null;
    }

    @Override
    public void updateAccount(String email, AccountDto dto) {

    }

    @Override
    public void changePassword(String email, String oldPassword, String newPassword) {

    }
}
