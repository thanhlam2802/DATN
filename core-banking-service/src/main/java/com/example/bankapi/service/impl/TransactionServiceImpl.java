package com.example.bankapi.service.impl;

import com.example.bankapi.model.entity.Transaction;
import com.example.bankapi.repository.TransactionRepository;
import com.example.bankapi.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;

    @Autowired
    public TransactionServiceImpl(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public List<Transaction> queryTransactions(Long accountId, LocalDate from, LocalDate to) {
        return transactionRepository.findByAccountIdAndBookingDateBetween(accountId, from, to);
    }
} 