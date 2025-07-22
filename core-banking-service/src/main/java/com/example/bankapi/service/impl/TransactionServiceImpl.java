package com.example.bankapi.service.impl;

import com.example.bankapi.model.entity.Transaction;
import com.example.bankapi.repository.TransactionRepository;
import com.example.bankapi.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import com.example.bankapi.model.dto.TransactionDto;

@Service
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;

    @Autowired
    public TransactionServiceImpl(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public List<TransactionDto> queryTransactions(String accountNumber, String bankCode, java.time.LocalDate from, java.time.LocalDate to) {
        List<Transaction> transactions = transactionRepository.findByAccount_AccountNumberAndAccount_BankCodeAndBookingDateBetween(accountNumber, bankCode, from, to);
        return transactions.stream().map(this::toTransactionDto).collect(java.util.stream.Collectors.toList());
    }

    public TransactionDto toTransactionDto(Transaction entity) {
        if (entity == null) return null;
        TransactionDto dto = new TransactionDto();
        dto.setTransactionId(entity.getTransactionId());
        dto.setBookingDate(entity.getBookingDate());
        dto.setAmount(entity.getAmount());
        dto.setDescription(entity.getDescription());
        return dto;
    }
} 