package backend.backend.entity;



import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Data
@Entity
@Table(name = "booking_tours")
public class BookingTour {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "departure_date_id", nullable = false)
    private Departure departure;

    @Column(name = "customer_name", length = 255)
    private String customerName;

    @Column(length = 20)
    private String phone;

    @Column(name = "number_of_adults")
    private Integer numberOfAdults;

    @Column(name = "number_of_children")
    private Integer numberOfChildren;

    @Column(name = "booking_date")
    private LocalDate bookingDate;

    @Lob
    private String notes;

    @OneToMany(mappedBy = "bookingTour")
    private List<Payment> payments;

    @OneToMany(mappedBy = "bookingTour")
    private List<TicketDetail> ticketDetails;
}