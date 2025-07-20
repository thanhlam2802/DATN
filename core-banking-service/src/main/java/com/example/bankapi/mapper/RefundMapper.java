package com.example.bankapi.mapper;

import com.example.bankapi.model.dto.RefundDto;
import com.example.bankapi.model.entity.Refund;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface RefundMapper {
    RefundMapper INSTANCE = Mappers.getMapper(RefundMapper.class);
    RefundDto toDto(Refund entity);
} 