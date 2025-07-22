package com.example.bankapi.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Schema(description = "Lịch sử giao dịch tài khoản")
public class TransactionDto {
    @Schema(example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID transactionId;
    @Schema(example = "2024-06-01")
    private LocalDate bookingDate;
    @Schema(example = "100000.00")
    private BigDecimal amount;
    @Schema(example = "Nhận tiền chuyển khoản")
    private String description;
} 