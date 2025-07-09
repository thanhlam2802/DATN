package backend.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "routes")
public class Route {
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 100)
    @NotNull
    @Nationalized
    @Column(name = "origin", nullable = false, length = 100)
    private String origin;

    @Size(max = 100)
    @NotNull
    @Nationalized
    @Column(name = "destination", nullable = false, length = 100)
    private String destination;

    @Column(name = "distance_km")
    private Double distanceKm;

    @Column(name = "estimated_duration_minutes")
    private Integer estimatedDurationMinutes;

    @NotNull
    @ColumnDefault("getdate()")
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @NotNull
    @ColumnDefault("getdate()")
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

}