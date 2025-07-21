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

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "Account")
@Schema(description = "Thông tin tài khoản ngân hàng")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID tài khoản (PK)", example = "1")
    private Long id;

    @Column(length = 10, nullable = false)
    @Schema(description = "Mã ngân hàng", example = "VCB")
    private String bankCode;

    @Column(length = 20, nullable = false, unique = true)
    @Schema(description = "Số tài khoản", example = "0123456789")
    private String accountNumber;

    @Column(length = 100, nullable = false)
    @Schema(description = "Tên chủ tài khoản", example = "Nguyen Van A")
    private String accountHolderName;

    @Column(length = 3, nullable = false)
    @Schema(description = "Loại tiền tệ", example = "VND")
    private String currency;

    @Column(nullable = false, precision = 18, scale = 2)
    @Schema(description = "Số dư khả dụng", example = "1000000.00")
    private BigDecimal availableBalance;

    @Column(nullable = false, precision = 18, scale = 2)
    @Schema(description = "Số dư hiện tại", example = "1000000.00")
    private BigDecimal currentBalance;

    @Column(nullable = false, precision = 18, scale = 2)
    @Schema(description = "Hạn mức thấu chi", example = "500000.00")
    private BigDecimal overdraftLimit;

    @Column(length = 100, nullable = false)
    @Schema(description = "Email chủ tài khoản", example = "user@example.com")
    private String email;

    @CreationTimestamp
    @Column(updatable = false)
    @Schema(description = "Thời điểm tạo", example = "2024-06-01T12:00:00Z")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Schema(description = "Thời điểm cập nhật cuối", example = "2024-06-01T12:00:00Z")
    private LocalDateTime updatedAt;
} 