package com.example.bankapi.repository;

import com.example.bankapi.model.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByAccountIdAndBookingDateBetween(Long accountId, LocalDate from, LocalDate to);
    Transaction findByTransactionId(UUID transactionId);
} 