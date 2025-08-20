package backend.backend.entity;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor

@Table(name = "bus_amenities")
public class BusAmenity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 100)
    private String name; // Tên tiện ích, ví dụ: "Wi-Fi", "Điều hòa", "Nhà vệ sinh"

    @Column(length = 500)
    private String description; // Mô tả thêm về tiện ích

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt = OffsetDateTime.now();

    // Mối quan hệ Many-to-Many với Bus
    // mappedBy chỉ ra rằng mối quan hệ được quản lý bởi trường 'amenities' trong entity Bus
    @ManyToMany(mappedBy = "amenities")
    @EqualsAndHashCode.Exclude
    private Set<Bus> buses = new HashSet<>();

    @PreUpdate
    protected void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }
}
