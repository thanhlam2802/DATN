    package com.example.bankapi.controller;

    import com.example.bankapi.model.dto.*;
    import com.example.bankapi.service.PaymentService;
    import io.swagger.v3.oas.annotations.Operation;
    import io.swagger.v3.oas.annotations.Parameter;
    import io.swagger.v3.oas.annotations.media.Content;
    import io.swagger.v3.oas.annotations.media.Schema;
    import io.swagger.v3.oas.annotations.responses.ApiResponse;
    import io.swagger.v3.oas.annotations.tags.Tag;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.data.redis.core.StringRedisTemplate;
    import org.springframework.data.redis.core.ValueOperations;
    import org.springframework.cloud.stream.function.StreamBridge;
    import com.example.bankapi.model.dto.OtpMailDto;
    import java.time.Duration;
    import java.time.LocalDateTime;
    import java.time.format.DateTimeFormatter;
    import java.util.Random;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;

    import java.util.UUID;
    import java.util.Map;
    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;
    import com.example.bankapi.model.dto.ServicePaymentRequestDto;


    @RestController
    @RequestMapping("/api/v1")
    @Tag(name = "Payment API", description = "API thanh toán, kiểm tra trạng thái, hoàn tiền")
    public class PaymentController {
        @Autowired
        private PaymentService paymentService;


        private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

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
            logger.info("[PaymentController] initiatePayment - Request: {}", req);
            PaymentDto paymentDto = paymentService.initiatePayment(
                    req.getDebtorAccountNumber(), req.getDebtorBankCode(), req.getCreditorAccountNumber(), req.getCreditorBankCode(), req.getAmount(), req.getCurrency(), req.getRemittanceInfo(), req.getIdempotencyKey()
            );
            logger.info("[PaymentController] initiatePayment - Result: {}", paymentDto);
            return ResponseEntity.ok(paymentDto);
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
            PaymentDto paymentDto = paymentService.getStatus(UUID.fromString(id));
            return paymentDto != null ? ResponseEntity.ok(paymentDto) : ResponseEntity.notFound().<PaymentDto>build();
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
            RefundDto refundDto = paymentService.refundPayment(UUID.fromString(id), req.getAmount(), req.getReason());
            return ResponseEntity.ok(refundDto);
        }

        @Operation(summary = "Thanh toán dịch vụ: chuyển tiền từ khách hàng vào tài khoản nguồn hệ thống",
                requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                        required = true,
                        content = @Content(schema = @Schema(
                            example = "{" +
                                    "\n  'customerAccountId': 2," +
                                    "\n  'amount': 50000.00," +
                                    "\n  'currency': 'VND'," +
                                    "\n  'remittanceInfo': 'Thanh toán dịch vụ'," +
                                    "\n  'idempotencyKey': 'abc123xyz'\n}")))
                ,
                responses = @ApiResponse(
                        responseCode = "200",
                        description = "Kết quả giao dịch",
                        content = @Content(schema = @Schema(implementation = PaymentDto.class))
                )
        )
        @PostMapping("/payments/service")
        public ResponseEntity<PaymentDto> payServicePayment(@RequestBody ServicePaymentRequestDto req) {
            logger.info("[PaymentController] payServicePayment - Request: {}", req);
            PaymentDto paymentDto = paymentService.payServicePayment(
                req.getCustomerAccountNumber(), req.getCustomerBankCode(), req.getAmount(), req.getCurrency(), req.getRemittanceInfo(), req.getIdempotencyKey()
            );
            logger.info("[PaymentController] payServicePayment - Result: {}", paymentDto);
            return ResponseEntity.ok(paymentDto);
        }



        @PostMapping("/payments/service/make")
        public ResponseEntity<PaymentDto> makeServicePayment(@RequestBody ServicePaymentRequestDto req) {
            PaymentDto dto = paymentService.makeServicePayment(req);
            return ResponseEntity.ok(dto);
        }

        @PostMapping("/payments/service/confirm")
        public ResponseEntity<TransactionDto> confirmServicePayment(@RequestBody Map<String, String> req) {
            String paymentId = req.get("paymentId");
            String otp = req.get("otp");
            TransactionDto dto = paymentService.confirmServicePayment(paymentId, otp);
            return ResponseEntity.ok(dto);
        }

        @Operation(summary = "Hủy vé - Gửi OTP xác nhận",
                requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                        required = true,
                        content = @Content(schema = @Schema(
                            example = "{" +
                                    "\n  'transactionId': 'uuid-1'," +
                                    "\n  'reason': 'Hủy vé do thay đổi lịch trình'\n}")))
                ,
                responses = @ApiResponse(
                        responseCode = "200",
                        description = "Kết quả gửi OTP",
                        content = @Content(schema = @Schema(implementation = PaymentDto.class))
                )
        )
        @PostMapping("/refunds/make/{transactionId}")
        public ResponseEntity<PaymentDto> makeRefund(@PathVariable String transactionId,
                                                     @RequestBody String reason) {
            UUID transaction = UUID.fromString(transactionId);
            PaymentDto paymentDto = paymentService.makeRefund(transaction, reason);
            return ResponseEntity.ok(paymentDto);
        }

        @Operation(summary = "Hủy vé - Xác nhận OTP",
                requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                        required = true,
                        content = @Content(schema = @Schema(
                            example = "{" +
                                    "\n  'paymentId': 'uuid-1'," +
                                    "\n  'otp': '123456'\n}")))
                ,
                responses = @ApiResponse(
                        responseCode = "200",
                        description = "Kết quả hoàn tiền",
                        content = @Content(schema = @Schema(implementation = RefundDto.class))
                )
        )
        @PostMapping("/refunds/confirm")
        public ResponseEntity<RefundDto> confirmRefund(@RequestBody Map<String, String> req) {
            String paymentId = req.get("paymentId");
            String otp = req.get("otp");
            RefundDto refundDto = paymentService.confirmRefund(paymentId, otp);
            return ResponseEntity.ok(refundDto);
        }
    }