package com.example.bankapi.mapper;

import com.example.bankapi.model.dto.TransactionDto;
import com.example.bankapi.model.entity.Transaction;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TransactionMapper {
    TransactionDto toDto(Transaction entity);
} 