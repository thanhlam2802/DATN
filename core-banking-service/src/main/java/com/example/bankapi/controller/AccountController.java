package com.example.bankapi.controller;

import com.example.bankapi.model.dto.AccountDto;
import com.example.bankapi.model.dto.AccountLookupRequestDto;
import com.example.bankapi.model.dto.TransactionDto;
import com.example.bankapi.service.AccountService;
import com.example.bankapi.service.TransactionService;
import com.example.bankapi.mapper.TransactionMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Account API", description = "API tra cứu tài khoản, số dư, lịch sử giao dịch")
public class AccountController {
    @Autowired
    private AccountService accountService;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private TransactionMapper transactionMapper;

    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);

    @Operation(summary = "Tra cứu thông tin tài khoản theo bankCode & accountNumber",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(schema = @Schema(implementation = AccountLookupRequestDto.class))
            ),
            responses = @ApiResponse(
                    responseCode = "200",
                    description = "Thông tin tài khoản",
                    content = @Content(schema = @Schema(implementation = AccountDto.class))
            )
    )
    @PostMapping("/account-lookup")
    public ResponseEntity<AccountDto> lookupAccount(@RequestBody AccountLookupRequestDto req) {
        logger.info("[AccountController] lookupAccount - Request: {}", req);
        AccountDto result = accountService.lookupAccountDto(req.getBankCode(), req.getAccountNumber());
        logger.info("[AccountController] lookupAccount - Result: {}", result);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Lấy số dư tài khoản",
            parameters = {
                    @Parameter(name = "id", description = "ID tài khoản", example = "1")
            },
            responses = @ApiResponse(
                    responseCode = "200",
                    description = "Số dư tài khoản",
                    content = @Content(schema = @Schema(implementation = AccountDto.class))
            )
    )
    @GetMapping("/accounts/{id}/balances")
    public ResponseEntity<AccountDto> getBalance(@PathVariable Long id) {
        logger.info("[AccountController] getBalance - id: {}", id);
        var result = accountService.lookupAccountById(id)
                .map(accountService::toDto)
                .orElse(null);
        logger.info("[AccountController] getBalance - Result: {}", result);
        return result != null ? ResponseEntity.ok(result) : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Lấy lịch sử giao dịch tài khoản",
            parameters = {
                    @Parameter(name = "id", description = "ID tài khoản", example = "1"),
                    @Parameter(name = "from", description = "Từ ngày", example = "2024-06-01"),
                    @Parameter(name = "to", description = "Đến ngày", example = "2024-06-30")
            },
            responses = @ApiResponse(
                    responseCode = "200",
                    description = "Danh sách giao dịch",
                    content = @Content(schema = @Schema(implementation = TransactionDto.class))
            )
    )
    @GetMapping("/accounts/{id}/transactions")
    public ResponseEntity<List<TransactionDto>> getTransactions(
            @PathVariable Long id,
            @RequestParam String from,
            @RequestParam String to) {
        logger.info("[AccountController] getTransactions - id: {}, from: {}, to: {}", id, from, to);
        LocalDate fromDate = LocalDate.parse(from);
        LocalDate toDate = LocalDate.parse(to);
        List<TransactionDto> dtos = transactionService.queryTransactions(id, fromDate, toDate)
                .stream().map(transactionMapper::toDto).collect(Collectors.toList());
        logger.info("[AccountController] getTransactions - Result: {}", dtos);
        return ResponseEntity.ok(dtos);
    }
} 