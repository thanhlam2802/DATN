package backend.backend.entity;



import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "ticket_details")
public class TicketDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_booking_id", nullable = false)
    private TicketBooking ticketBooking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_bus_id")
    private BusBooking busBooking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_flight_id")
    private FlightBooking flightBooking;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_tour_id")
    private BookingTour bookingTour;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_booking_id")
    private HotelBooking hotelBooking;

    @Column(nullable = false, length = 50)
    private String status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}