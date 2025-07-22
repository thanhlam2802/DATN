// backend.backend.entity.Bus.java
package backend.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "buses")
public class Bus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 200)
    private String name; // Tên xe, ví dụ: "Xe giường nằm cao cấp 45 chỗ"

    @Column(name = "license_plate", nullable = false, length = 20, unique = true)
    private String licensePlate; // Biển số xe (rất quan trọng)

    @Column(name = "total_seats", nullable = false)
    private Integer totalSeats; // Tổng số ghế

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    @ToString.Include
    private BusCategory category;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_id", nullable = false) // Đảm bảo owner_id là NOT NULL
    @ToString.Include
    private User owner; // Chủ sở hữu/Nhà xe

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt = OffsetDateTime.now();

    @OneToMany(mappedBy = "bus")
    @ToString.Include
    private List<BusSlot> busSlots; // Các chuyến đi mà xe này thực hiện

    // Thêm cascade và orphanRemoval để khi xóa Bus sẽ xóa luôn BusImage liên quan
    @OneToMany(mappedBy = "bus", orphanRemoval = true)
    @ToString.Include
    private List<BusImage> busImages = new ArrayList<>(); // Ảnh của chiếc xe bus này

    @OneToMany(mappedBy = "bus")
    @ToString.Include
    private List<BusRoute> busRoutes; // Các tuyến đường mà chiếc xe này được phép chạy

    @PreUpdate
    protected void onUpdate() {
        updatedAt = OffsetDateTime.now().now();
    }
}