package com.example.bankapi.mapper;

import com.example.bankapi.model.dto.PaymentDto;
import com.example.bankapi.model.entity.Payment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PaymentMapper {
    PaymentDto toDto(Payment entity);
} 