package backend.backend.entity;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "route_bus_category_prices", uniqueConstraints = {
        // UniqueConstraint sẽ phức tạp hơn nếu có hiệu lực từ/đến.
        // Tạm thời bỏ unique constraint nếu có hiệu lực để cho phép nhiều giá cho cùng cặp tuyến/loại xe
        // trong các khoảng thời gian khác nhau.
        // @UniqueConstraint(columnNames = {"route_id", "bus_category_id"})
})
public class RouteBusCategoryPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bus_category_id", nullable = false)
    private BusCategory busCategory;

    @Column(name = "base_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal basePrice;

    @Column(name = "promotion_price", precision = 12, scale = 2) // Nullable
    private BigDecimal promotionPrice;

    @Column(name = "valid_from", nullable = false)
    private LocalDate validFrom; // Ngày bắt đầu hiệu lực

    @Column(name = "valid_to", nullable = false)
    private LocalDate validTo; // Ngày kết thúc hiệu lực

    @Column(name = "notes", length = 500) // Nullable
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
