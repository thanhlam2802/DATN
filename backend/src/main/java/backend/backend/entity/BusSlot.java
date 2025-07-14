package backend.backend.entity;



import lombok.Data;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Entity
@Table(name = "bus_slots")
public class BusSlot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bus_id", nullable = false)
    private Bus bus; // Chiếc xe bus chạy chuyến này

    @ManyToOne(fetch = FetchType.LAZY) // <-- THÊM MỐI QUAN HỆ NÀY
    @JoinColumn(name = "route_id", nullable = false) // <-- Cột khóa ngoại đến bảng routes
    private Route route; // Tuyến đường cụ thể của slot này (ví dụ: HN-HCM)

    @Column(name = "slot_date", nullable = false)
    private LocalDate slotDate; // Ngày của chuyến đi cụ thể

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price; // Giá vé cho chuyến đi cụ thể này

    @Column(name = "total_seats")
    private Integer totalSeats; // Tổng số ghế trên chuyến đi này

    @OneToMany(mappedBy = "busSlot")
    private List<BusBooking> busBookings;
}
