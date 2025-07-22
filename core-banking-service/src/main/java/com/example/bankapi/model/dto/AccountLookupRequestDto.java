package com.example.bankapi.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Yêu cầu tra cứu tài khoản")
public class AccountLookupRequestDto {
    @Schema(example = "VCB")
    private String bankCode;
    @Schema(example = "0123456789")
    private String accountNumber;
} 