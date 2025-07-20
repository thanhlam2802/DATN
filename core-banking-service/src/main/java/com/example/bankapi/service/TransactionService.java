package com.example.bankapi.service;

import com.example.bankapi.model.entity.Transaction;
import com.example.bankapi.model.dto.TransactionDto;

import java.time.LocalDate;
import java.util.List;

public interface TransactionService {
    List<TransactionDto> queryTransactions(String accountNumber, String bankCode, java.time.LocalDate from, java.time.LocalDate to);
} 