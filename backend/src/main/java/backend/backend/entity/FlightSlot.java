package backend.backend.entity;



import lombok.Data;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Data
@Entity
@Table(name = "flight_slots")
public class FlightSlot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flight_id", nullable = false)
    private Flight flight;

    @Column(name = "seat_class", nullable = false, length = 50)
    private String seatClass;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    private Integer capacity;

    @OneToMany(mappedBy = "flightSlot")
    private List<FlightBooking> flightBookings;
}