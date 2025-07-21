package com.example.bankapi.mapper;

import com.example.bankapi.model.dto.TransactionDto;
import com.example.bankapi.model.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface TransactionMapper {
    TransactionMapper INSTANCE = Mappers.getMapper(TransactionMapper.class);
    TransactionDto toDto(Transaction entity);
} 