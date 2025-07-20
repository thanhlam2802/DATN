package com.example.bankapi.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Schema(description = "Kết quả hoàn tiền giao dịch")
public class RefundDto {
    @Schema(example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID refundId;
    @Schema(example = "PENDING")
    private String status;
    @Schema(example = "10000.00")
    private BigDecimal amount;
    @Schema(example = "2024-06-01T13:00:00Z")
    private LocalDateTime createdAt;
} 