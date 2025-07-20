package com.example.bankapi.mapper;

import com.example.bankapi.model.dto.AccountDto;
import com.example.bankapi.model.entity.Account;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AccountMapper {
    AccountDto toDto(Account entity);
} 