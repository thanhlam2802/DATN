package backend.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "vouchers")
public class Voucher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // --- Thông tin cơ bản ---
    @Column(nullable = false, unique = true, length = 50)
    private String code; 

    @Column(nullable = false, length = 255)
    private String name; 

    @Lob
    private String description; // Mô tả chi tiết

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private VoucherStatus status = VoucherStatus.ACTIVE; 

    // --- Loại và Mức giảm giá ---
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private VoucherType type; 

    @Column(name = "discount_amount", precision = 12, scale = 2)
    private BigDecimal discountAmount; // Số tiền giảm (dùng cho type = FIXED_AMOUNT)

    @Column(name = "discount_percentage")
    private Integer discountPercentage; // Tỷ lệ phần trăm giảm (dùng cho type = PERCENTAGE)

    @Column(name = "max_discount_amount", precision = 12, scale = 2)
    private BigDecimal maxDiscountAmount; // Số tiền giảm tối đa (dùng cho type = PERCENTAGE)

    // --- Điều kiện áp dụng ---
    @Column(name = "condition_min_amount", precision = 12, scale = 2)
    private BigDecimal conditionMinAmount; // Giá trị đơn hàng tối thiểu để áp dụng

    // --- Thời gian hiệu lực ---
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate; // Ngày bắt đầu có hiệu lực

    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate;

    // --- Giới hạn sử dụng ---
    @Column(name = "usage_limit")
    private Integer usageLimit; 

    @Column(name = "user_usage_limit", columnDefinition = "int default 1")
    private Integer userUsageLimit; 

    @Column(name = "usage_count", columnDefinition = "int default 0")
    private Integer usageCount = 0; 

    // --- Dấu thời gian ---
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // --- Quan hệ ---
    @OneToMany(mappedBy = "voucher")
    private List<UserVoucher> userVouchers;

    @OneToMany(mappedBy = "voucher")
    private List<Order> orders;
}