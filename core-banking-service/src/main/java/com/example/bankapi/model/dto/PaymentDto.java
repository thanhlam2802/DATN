package com.example.bankapi.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Schema(description = "Kết quả giao dịch thanh toán")
public class PaymentDto {
    @Schema(example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID paymentId;
    @Schema(example = "PENDING")
    private String status;
    @Schema(example = "50000.00")
    private BigDecimal amount;
    @Schema(example = "VND")
    private String currency;
    @Schema(example = "2024-06-01T12:00:00Z")
    private LocalDateTime createdAt;
} 