package com.example.bankapi.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "Thông tin tài khoản trả về cho client")
public class AccountDto {
    @Schema(example = "VCB")
    private String bankCode;
    @Schema(example = "0123456789")
    private String accountNumber;
    @Schema(example = "Nguyen Van A")
    private String accountHolderName;
    @Schema(example = "VND")
    private String currency;
    @Schema(example = "1000000.00")
    private BigDecimal availableBalance;
    @Schema(example = "1000000.00")
    private BigDecimal currentBalance;
    @Schema(example = "500000.00")
    private BigDecimal overdraftLimit;
} 