package backend.backend.entity;



import backend.backend.entity.enumBus.BusBookingStatus;
import lombok.Data;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "bus_bookings")
public class BusBooking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bus_slot_id", nullable = false)
    private BusSlot busSlot;

    // ✅ ADD: Customer relationship
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    // ✅ ADD: Selected seats relationship
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "bus_booking_seats",
            joinColumns = @JoinColumn(name = "booking_id"),
            inverseJoinColumns = @JoinColumn(name = "seat_id")
    )
    private List<BusSeat> selectedSeats;

    // ✅ ADD: Booking status
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private BusBookingStatus status = BusBookingStatus.RESERVED;

    // ✅ ADD: Booking reference
    @Column(name = "booking_reference", nullable = false, unique = true, length = 20)
    private String bookingReference;

    @Column(name = "num_passengers", nullable = false)
    private Integer numPassengers;

    @Column(name = "total_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalPrice;

    @Column(name = "booking_date", nullable = false, updatable = false)
    private LocalDateTime bookingDate = LocalDateTime.now();

    // ✅ ADD: Expiration for temporary bookings
    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    // ✅ ADD: Notes
    @Column(name = "notes", length = 500)
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @PrePersist
    protected void onCreate() {
        if (bookingReference == null) {
            bookingReference = "BUS" + System.currentTimeMillis();
        }
        if (numPassengers == null && selectedSeats != null) {
            numPassengers = selectedSeats.size();
        }
    }
}