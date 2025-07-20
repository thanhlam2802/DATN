package com.example.bankapi.repository;

import com.example.bankapi.model.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByAccount_AccountNumberAndAccount_BankCodeAndBookingDateBetween(String accountNumber, String bankCode, java.time.LocalDate from, java.time.LocalDate to);
    Transaction findByTransactionId(UUID transactionId);
} 