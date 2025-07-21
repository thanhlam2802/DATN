package com.example.bankapi.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Schema(description = "Yêu cầu thanh toán dịch vụ (khách hàng nạp vào hệ thống)")
public class ServicePaymentRequestDto {
    @Schema(example = "11111")
    private String customerAccountNumber;
    @Schema(example = "MB Bank")
    private String customerBankCode;
    @Schema(example = "50000.00")
    private BigDecimal amount;
    @Schema(example = "VND")
    private String currency;
    @Schema(example = "Thanh toán dịch vụ")
    private String remittanceInfo;
    @Schema(example = "abc123xyz")
    private String idempotencyKey;
} 