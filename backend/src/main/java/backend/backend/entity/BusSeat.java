package backend.backend.entity;

import backend.backend.entity.enumBus.BusSeatType; // Đã đổi tên enum
import lombok.Data;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "bus_seats") // Đã đổi tên bảng
public class BusSeat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "seat_number", nullable = false, length = 10)
    private String seatNumber; // Ví dụ: "A1", "B2", "1", "2"

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bus_slot_id", nullable = false)
    private BusSlot busSlot; // Ghế này thuộc về chuyến xe nào

    @Column(name = "is_booked", nullable = false)
    private Boolean isBooked = false; // Trạng thái đã đặt hay chưa

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price; // Giá của ghế này (có thể khác nhau tùy loại ghế)

    @Enumerated(EnumType.STRING)
    @Column(name = "seat_type", length = 20)
    private BusSeatType seatType; // Loại ghế (ví dụ: STANDARD, VIP, AISLE, WINDOW)

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