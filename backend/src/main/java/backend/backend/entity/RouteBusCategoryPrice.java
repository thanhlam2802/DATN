package backend.backend.entity;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal; // Import nếu bạn sử dụng BigDecimal
import java.time.Instant;    // ĐẢM BẢO IMPORT Instant
import java.time.LocalDate;  // Import nếu bạn sử dụng LocalDate cho validFrom/validTo

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "route_bus_category_prices")
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

    // ✅ THÊM: Owner ID để đảm bảo multi-tenant isolation
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(name = "base_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal basePrice;

    @Column(name = "promotion_price", precision = 10, scale = 2)
    private BigDecimal promotionPrice;

    @Column(name = "valid_from", nullable = false)
    private LocalDate validFrom; // Ví dụ, sử dụng LocalDate cho ngày

    @Column(name = "valid_to", nullable = false)
    private LocalDate validTo;   // Ví dụ, sử dụng LocalDate cho ngày

    @Column(name = "notes", length = 255)
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt; // ĐẢM BẢO LÀ Instant

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt; // ĐẢM BẢO LÀ Instant

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}
