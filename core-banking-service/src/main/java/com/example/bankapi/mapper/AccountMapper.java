package com.example.bankapi.mapper;

import com.example.bankapi.model.dto.AccountDto;
import com.example.bankapi.model.entity.Account;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AccountMapper {
    AccountMapper INSTANCE = Mappers.getMapper(AccountMapper.class);
    AccountDto toDto(Account entity);
} 