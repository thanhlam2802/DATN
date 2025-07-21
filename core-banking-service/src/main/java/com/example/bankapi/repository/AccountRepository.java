package com.example.bankapi.repository;

import com.example.bankapi.model.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByBankCodeAndAccountNumber(String bankCode, String accountNumber);
} 