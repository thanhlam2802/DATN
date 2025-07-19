package com.example.bankapi.service;

import com.example.bankapi.model.entity.Account;
import com.example.bankapi.model.dto.AccountDto;
import java.util.Optional;

public interface AccountService {
    AccountDto lookupAccountDto(String bankCode, String accountNumber);
    Optional<Account> lookupAccountById(Long id);
    AccountDto toDto(Account entity);
} 