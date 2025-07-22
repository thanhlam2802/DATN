package com.example.bankapi.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "Yêu cầu hoàn tiền giao dịch")
public class RefundRequestDto {
    @Schema(example = "10000.00")
    private BigDecimal amount;
    @Schema(example = "Khách hàng yêu cầu hoàn")
    private String reason;
} 