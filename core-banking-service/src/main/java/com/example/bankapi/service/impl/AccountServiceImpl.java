package com.example.bankapi.service.impl;

import com.example.bankapi.model.entity.Account;
import com.example.bankapi.repository.AccountRepository;
import com.example.bankapi.service.AccountService;
import com.example.bankapi.mapper.AccountMapper;
import com.example.bankapi.model.dto.AccountDto;
import com.example.bankapi.exception.AccountNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private static final Logger logger = LoggerFactory.getLogger(AccountServiceImpl.class);

    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }


    @Override
    public AccountDto lookupAccountDto(String bankCode, String accountNumber) {
        logger.info("[AccountService] lookupAccountDto - bankCode: {}, accountNumber: {}", bankCode, accountNumber);
        Account account = accountRepository.findByBankCodeAndAccountNumber(bankCode, accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Không tìm thấy tài khoản"));
        logger.info("[AccountService] lookupAccountDto - Result: id={}, bankCode={}, accountNumber={}, accountHolderName={}, currency={}, availableBalance={}, currentBalance={}, overdraftLimit={}, createdAt={}, updatedAt={}",
            account.getId(), account.getBankCode(), account.getAccountNumber(), account.getAccountHolderName(), account.getCurrency(), account.getAvailableBalance(), account.getCurrentBalance(), account.getOverdraftLimit(), account.getCreatedAt(), account.getUpdatedAt()
        );
        return accountMapper.toDto(account);
    }



    @Override
    public Optional<Account> lookupAccountById(Long id) {
        logger.info("[AccountService] lookupAccountById - id: {}", id);
        Optional<Account> result = accountRepository.findById(id);
        result.ifPresent(account -> logger.info("[AccountService] lookupAccountById - Result: id={}, bankCode={}, accountNumber={}, accountHolderName={}, currency={}, availableBalance={}, currentBalance={}, overdraftLimit={}, createdAt={}, updatedAt={}",
            account.getId(), account.getBankCode(), account.getAccountNumber(), account.getAccountHolderName(), account.getCurrency(), account.getAvailableBalance(), account.getCurrentBalance(), account.getOverdraftLimit(), account.getCreatedAt(), account.getUpdatedAt()
        ));
        return result;
    }

    @Override
    public AccountDto toDto(Account entity) {
        return accountMapper.toDto(entity);
    }
} 