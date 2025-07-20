package com.example.bankapi.model.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "Refund")
@Schema(description = "Thông tin hoàn tiền giao dịch")
public class Refund {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID hoàn tiền (PK)", example = "1")
    private Long id;

    @Column(nullable = false, unique = true)
    @Schema(description = "Mã hoàn tiền (UUID)", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID refundId = UUID.randomUUID();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paymentDbId", nullable = false)
    @Schema(description = "Giao dịch thanh toán liên quan")
    private Payment payment;

    @Column(nullable = false, precision = 18, scale = 2)
    @Schema(description = "Số tiền hoàn", example = "100000.00")
    private BigDecimal amount;

    @Column(length = 255)
    @Schema(description = "Lý do hoàn tiền", example = "Khách hàng yêu cầu hoàn")
    private String reason;

    @Column(length = 20, nullable = false)
    @Schema(description = "Trạng thái hoàn tiền", example = "PENDING")
    private String status;

    @CreationTimestamp
    @Column(updatable = false)
    @Schema(description = "Thời điểm tạo bản ghi", example = "2024-06-01T12:00:00Z")
    private LocalDateTime createdAt;
} 