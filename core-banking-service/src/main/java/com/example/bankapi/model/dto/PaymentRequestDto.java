package com.example.bankapi.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "Yêu cầu khởi tạo thanh toán")
public class PaymentRequestDto {
    @Schema(example = "11111")
    private String debtorAccountNumber;
    @Schema(example = "MB Bank")
    private String debtorBankCode;
    @Schema(example = "22222")
    private String creditorAccountNumber;
    @Schema(example = "Techcombank")
    private String creditorBankCode;
    @Schema(example = "50000.00")
    private BigDecimal amount;
    @Schema(example = "VND")
    private String currency;
    @Schema(example = "Thanh toán vé máy bay")
    private String remittanceInfo;
    @Schema(example = "abc123xyz")
    private String idempotencyKey;
} 