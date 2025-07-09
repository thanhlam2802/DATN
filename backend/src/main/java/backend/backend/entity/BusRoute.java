package backend.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "bus_routes")
public class BusRoute {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bus_id", nullable = false)
    private Bus bus;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;

    @Column(name = "travel_date")
    private Instant travelDate;

    @Column(name = "price", precision = 10, scale = 2)
    private BigDecimal price;

    @Size(max = 50)
    @Nationalized
    @Column(name = "status", length = 50)
    private String status;

    @NotNull
    @ColumnDefault("getdate()")
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @NotNull
    @ColumnDefault("getdate()")
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

}