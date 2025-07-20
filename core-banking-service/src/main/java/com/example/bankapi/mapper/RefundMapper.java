package com.example.bankapi.mapper;

import com.example.bankapi.model.dto.RefundDto;
import com.example.bankapi.model.entity.Refund;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RefundMapper {
    RefundDto toDto(Refund entity);
} 