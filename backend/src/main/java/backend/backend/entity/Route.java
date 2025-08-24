package backend.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "routes")
public class Route {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Thêm vào trong class Route.java, bên dưới các trường khác
    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "origin_location_id", nullable = false) // Đây là khóa ngoại trỏ tới bảng 'locations'
    private Location originLocation; // Điểm xuất phát của tuyến đường

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "destination_location_id", nullable = false) // Đây là khóa ngoại trỏ tới bảng 'locations'
    private Location destinationLocation; // Điểm đến của tuyến đường
    
    // ✅ THÊM MỚI: Route thuộc về doanh nghiệp/nhà xe
    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner; // Route thuộc về doanh nghiệp này
    
    @Column(name = "distance_km")
    private Double distanceKm;

    @Column(name = "estimated_duration_minutes")
    private Integer estimatedDurationMinutes;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @NotNull
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @OneToMany(mappedBy = "route")
    private List<BusRoute> busRoutes;

    @PrePersist
    public void prePersist() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = Instant.now();
    }

}