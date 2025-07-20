package com.example.bankapi.model.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "Payment")
@Schema(description = "Thông tin giao dịch thanh toán")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID nội bộ (PK)", example = "1")
    private Long id;

    @Column(nullable = false, unique = true)
    @Schema(description = "Mã giao dịch thanh toán (UUID)", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID paymentId = UUID.randomUUID();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "debtorAccountId", nullable = false)
    @Schema(description = "Tài khoản ghi nợ")
    private Account debtorAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creditorAccountId", nullable = false)
    @Schema(description = "Tài khoản ghi có")
    private Account creditorAccount;

    @Column(nullable = false, precision = 18, scale = 2)
    @Schema(description = "Số tiền giao dịch", example = "500000.00")
    private BigDecimal amount;

    @Column(length = 3, nullable = false)
    @Schema(description = "Loại tiền tệ", example = "VND")
    private String currency;

    @Column(length = 20, nullable = false)
    @Schema(description = "Trạng thái giao dịch", example = "PENDING")
    private String status;

    @Column(length = 255)
    @Schema(description = "Thông tin chuyển khoản", example = "Thanh toán vé máy bay")
    private String remittanceInfo;

    @Column(length = 50, nullable = false)
    @Schema(description = "Idempotency Key", example = "abc123xyz")
    private String idempotencyKey;

    @CreationTimestamp
    @Column(updatable = false)
    @Schema(description = "Thời điểm tạo", example = "2024-06-01T12:00:00Z")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Schema(description = "Thời điểm cập nhật cuối", example = "2024-06-01T12:00:00Z")
    private LocalDateTime updatedAt;
} 