package com.example.bankapi.service;

import com.example.bankapi.model.entity.Transaction;

import java.time.LocalDate;
import java.util.List;

public interface TransactionService {
    List<Transaction> queryTransactions(Long accountId, LocalDate from, LocalDate to);
} 