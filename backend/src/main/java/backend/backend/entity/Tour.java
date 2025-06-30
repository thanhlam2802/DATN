package backend.backend.entity;




import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.*;
import lombok.*;


@Data
@Entity
@Table(name = "tours")
public class Tour {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Lob
    private String description;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(name = "duration_days", nullable = false)
    private Integer durationDays;

    @Column(name = "departure_point", length = 200)
    private String departurePoint;

    @Column(length = 200)
    private String destination;

    /**
     * Thay đổi: Sử dụng Enum TourStatus thay vì String.
     * @Enumerated(EnumType.STRING) giúp lưu tên của enum ("ACTIVE", "INACTIVE") vào DB,
     * làm cho dữ liệu trong DB dễ đọc và an toàn hơn khi thay đổi thứ tự enum.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private TourStatus status = TourStatus.ACTIVE;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "tour")
    private List<TourImage> tourImages;

    @OneToMany(mappedBy = "tour")
    private List<TourSchedule> tourSchedules;

    @OneToMany(mappedBy = "tour")
    private List<Departure> departures;
}