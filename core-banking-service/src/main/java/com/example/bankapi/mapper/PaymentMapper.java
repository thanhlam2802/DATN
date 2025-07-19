package com.example.bankapi.mapper;

import com.example.bankapi.model.dto.PaymentDto;
import com.example.bankapi.model.entity.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface PaymentMapper {
    PaymentMapper INSTANCE = Mappers.getMapper(PaymentMapper.class);
    PaymentDto toDto(Payment entity);
} 