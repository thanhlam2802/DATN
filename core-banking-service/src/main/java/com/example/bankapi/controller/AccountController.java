package com.example.bankapi.controller;

import com.example.bankapi.model.dto.AccountDto;
import com.example.bankapi.model.dto.TransactionDto;
import com.example.bankapi.service.AccountService;
import com.example.bankapi.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Account API", description = "API tra cứu tài khoản, số dư, lịch sử giao dịch")
public class AccountController {
    @Autowired
    private AccountService accountService;
    @Autowired
    private TransactionService transactionService;

    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);

    @Operation(summary = "Tra cứu thông tin tài khoản theo bankCode & accountNumber",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(schema = @Schema(implementation = com.example.bankapi.model.dto.AccountLookupRequestDto.class))
            ),
            responses = @ApiResponse(
                    responseCode = "200",
                    description = "Thông tin tài khoản",
                    content = @Content(schema = @Schema(implementation = AccountDto.class))
            )
    )
    @PostMapping("/account-lookup")
    public ResponseEntity<AccountDto> lookupAccount(@RequestBody com.example.bankapi.model.dto.AccountLookupRequestDto req) {
        logger.info("[AccountController] lookupAccount - Request: {}", req);
        AccountDto result = accountService.lookupAccountDto(req.getBankCode(), req.getAccountNumber());
        logger.info("[AccountController] lookupAccount - Result: {}", result);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Lấy số dư tài khoản",
            parameters = {
                    @Parameter(name = "accountNumber", description = "Số tài khoản", example = "11111"),
                    @Parameter(name = "bankCode", description = "Mã ngân hàng", example = "MB Bank")
            },
            responses = @ApiResponse(
                    responseCode = "200",
                    description = "Số dư tài khoản",
                    content = @Content(schema = @Schema(implementation = AccountDto.class))
            )
    )
    @GetMapping("/accounts/balances")
    public ResponseEntity<AccountDto> getBalance(@RequestParam String accountNumber, @RequestParam String bankCode) {
        logger.info("[AccountController] getBalance - accountNumber: {}, bankCode: {}", accountNumber, bankCode);
        AccountDto result = accountService.lookupAccountDto(bankCode, accountNumber);
        logger.info("[AccountController] getBalance - Result: {}", result);
        return result != null ? ResponseEntity.ok(result) : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Lấy lịch sử giao dịch tài khoản",
            parameters = {
                    @Parameter(name = "accountNumber", description = "Số tài khoản", example = "11111"),
                    @Parameter(name = "bankCode", description = "Mã ngân hàng", example = "MB Bank"),
                    @Parameter(name = "from", description = "Từ ngày", example = "2024-06-01"),
                    @Parameter(name = "to", description = "Đến ngày", example = "2024-06-30")
            },
            responses = @ApiResponse(
                    responseCode = "200",
                    description = "Danh sách giao dịch",
                    content = @Content(schema = @Schema(implementation = TransactionDto.class))
            )
    )
    @GetMapping("/accounts/transactions")
    public ResponseEntity<List<TransactionDto>> getTransactions(
            @RequestParam String accountNumber,
            @RequestParam String bankCode,
            @RequestParam String from,
            @RequestParam String to) {
        logger.info("[AccountController] getTransactions - accountNumber: {}, bankCode: {}, from: {}, to: {}", accountNumber, bankCode, from, to);
        LocalDate fromDate = LocalDate.parse(from);
        LocalDate toDate = LocalDate.parse(to);
        List<TransactionDto> dtos = transactionService.queryTransactions(accountNumber, bankCode, fromDate, toDate);
        logger.info("[AccountController] getTransactions - Result: {}", dtos);
        return ResponseEntity.ok(dtos);
    }
} 