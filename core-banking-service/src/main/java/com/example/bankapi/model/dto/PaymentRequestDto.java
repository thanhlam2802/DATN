package com.example.bankapi.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "Yêu cầu khởi tạo thanh toán")
public class PaymentRequestDto {
    @Schema(example = "1")
    private Long debtorAccountId;
    @Schema(example = "2")
    private Long creditorAccountId;
    @Schema(example = "50000.00")
    private BigDecimal amount;
    @Schema(example = "VND")
    private String currency;
    @Schema(example = "Thanh toán vé máy bay")
    private String remittanceInfo;
    @Schema(example = "abc123xyz")
    private String idempotencyKey;
} 