package com.example.bankapi.service.impl;

import com.example.bankapi.model.entity.Account;
import com.example.bankapi.repository.AccountRepository;
import com.example.bankapi.service.AccountService;
import com.example.bankapi.mapper.AccountMapper;
import com.example.bankapi.model.dto.AccountDto;
import com.example.bankapi.exception.AccountNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;

    @Autowired
    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }


    @Override
    public AccountDto lookupAccountDto(String bankCode, String accountNumber) {
        Account account = accountRepository.findByBankCodeAndAccountNumber(bankCode, accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Không tìm thấy tài khoản"));
        return AccountMapper.INSTANCE.toDto(account);
    }

    @Override
    public Optional<Account> lookupAccountById(Long id) {
        return accountRepository.findById(id);
    }

    @Override
    public AccountDto toDto(Account entity) {
        return AccountMapper.INSTANCE.toDto(entity);
    }
} 