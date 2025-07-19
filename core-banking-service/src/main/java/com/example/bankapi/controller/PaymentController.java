package com.example.bankapi.controller;

import com.example.bankapi.model.dto.PaymentRequestDto;
import com.example.bankapi.model.dto.PaymentDto;
import com.example.bankapi.model.dto.RefundRequestDto;
import com.example.bankapi.model.dto.RefundDto;
import com.example.bankapi.service.PaymentService;
import com.example.bankapi.mapper.PaymentMapper;
import com.example.bankapi.mapper.RefundMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Payment API", description = "API thanh toán, kiểm tra trạng thái, hoàn tiền")
public class PaymentController {
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private PaymentMapper paymentMapper;
    @Autowired
    private RefundMapper refundMapper;

    @Operation(summary = "Khởi tạo giao dịch thanh toán",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(schema = @Schema(implementation = PaymentRequestDto.class))
            ),
            responses = @ApiResponse(
                    responseCode = "200",
                    description = "Kết quả giao dịch",
                    content = @Content(schema = @Schema(implementation = PaymentDto.class))
            )
    )
    @PostMapping("/payments")
    public ResponseEntity<PaymentDto> initiatePayment(@RequestBody PaymentRequestDto req) {
        var payment = paymentService.initiatePayment(
                req.getDebtorAccountId(), req.getCreditorAccountId(), req.getAmount(), req.getCurrency(), req.getRemittanceInfo(), req.getIdempotencyKey()
        );
        return ResponseEntity.ok(paymentMapper.toDto(payment));
    }

    @Operation(summary = "Lấy trạng thái giao dịch thanh toán",
            parameters = {
                    @Parameter(name = "id", description = "UUID giao dịch", example = "uuid-1")
            },
            responses = @ApiResponse(
                    responseCode = "200",
                    description = "Trạng thái giao dịch",
                    content = @Content(schema = @Schema(implementation = PaymentDto.class))
            )
    )
    @GetMapping("/payments/{id}/status")
    public ResponseEntity<PaymentDto> getStatus(@PathVariable String id) {
        return paymentService.getStatus(UUID.fromString(id))
                .map(paymentMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Hủy giao dịch thanh toán",
            parameters = {
                    @Parameter(name = "id", description = "UUID giao dịch", example = "uuid-1")
            },
            responses = @ApiResponse(
                    responseCode = "200",
                    description = "Kết quả hủy giao dịch",
                    content = @Content(schema = @Schema(implementation = PaymentDto.class))
            )
    )
    @DeleteMapping("/payments/{id}")
    public ResponseEntity<Void> cancelPayment(@PathVariable String id) {
        paymentService.cancelPayment(UUID.fromString(id));
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Hoàn tiền giao dịch",
            parameters = {
                    @Parameter(name = "id", description = "UUID giao dịch", example = "uuid-1")
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(schema = @Schema(implementation = RefundRequestDto.class))
            ),
            responses = @ApiResponse(
                    responseCode = "200",
                    description = "Kết quả hoàn tiền",
                    content = @Content(schema = @Schema(implementation = RefundDto.class))
            )
    )
    @PostMapping("/payments/{id}/refunds")
    public ResponseEntity<RefundDto> refundPayment(@PathVariable String id, @RequestBody RefundRequestDto req) {
        var refund = paymentService.refundPayment(UUID.fromString(id), req.getAmount(), req.getReason());
        return ResponseEntity.ok(refundMapper.toDto(refund));
    }
} 