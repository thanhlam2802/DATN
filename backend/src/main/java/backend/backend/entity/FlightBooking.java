package backend.backend.entity;



import lombok.Data;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "flight_bookings")
public class FlightBooking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "booking_date", nullable = false)
    private LocalDateTime bookingDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flight_slot_id", nullable = false)
    private FlightSlot flightSlot;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_detail_id")
    private TicketDetail ticketDetail;
}