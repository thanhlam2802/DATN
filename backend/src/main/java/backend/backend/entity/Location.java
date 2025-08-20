package backend.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.Instant; // ĐÃ THAY ĐỔI: Sử dụng Instant thay vì OffsetDateTime

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "locations")
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 200)
    @NotNull
    @Column(name = "name", nullable = false, length = 200, unique = true)
    private String name;

    @Size(max = 200)
    @Column(name = "province_city", length = 200)
    private String provinceCity;

    @Size(max = 200)
    @Column(name = "district", length = 200)
    private String district;

    @Size(max = 500)
    @Column(name = "address_details", length = 500)
    private String addressDetails;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt; // ĐÃ THAY ĐỔI: Kiểu Instant

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt; // ĐÃ THAY ĐỔI: Kiểu Instant

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now(); // ĐÃ THAY ĐỔI: Dùng Instant.now()
        updatedAt = Instant.now(); // ĐÃ THAY ĐỔI: Dùng Instant.now()
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now(); // ĐÃ THAY ĐỔI: Dùng Instant.now()
    }
}