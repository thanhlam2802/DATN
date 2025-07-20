package com.example.bankapi.model.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "Transaction")
@Schema(description = "Lịch sử giao dịch tài khoản")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID giao dịch (PK)", example = "1")
    private Long id;

    @Column(nullable = false, unique = true)
    @Schema(description = "Mã giao dịch (UUID)", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID transactionId = UUID.randomUUID();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accountId", nullable = false)
    @Schema(description = "Tài khoản liên quan")
    private Account account;

    @Column(nullable = false)
    @Schema(description = "Ngày ghi sổ", example = "2024-06-01")
    private LocalDate bookingDate;

    @Column(nullable = false, precision = 18, scale = 2)
    @Schema(description = "Số tiền giao dịch", example = "100000.00")
    private BigDecimal amount;

    @Column(length = 255)
    @Schema(description = "Mô tả giao dịch", example = "Nhận tiền chuyển khoản")
    private String description;

    @CreationTimestamp
    @Column(updatable = false)
    @Schema(description = "Thời điểm tạo bản ghi", example = "2024-06-01T12:00:00Z")
    private LocalDateTime createdAt;
} 