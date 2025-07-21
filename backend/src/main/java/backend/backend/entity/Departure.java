package backend.backend.entity;

import jakarta.persistence.*;

import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Entity
@Table(name = "departures", uniqueConstraints = {
	    @UniqueConstraint(columnNames = {"tour_id", "departure_date"})
	})
public class Departure {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    @JoinColumn(name = "tour_id", nullable = false)
    private Tour tour;

    @Column(name = "departure_date")
    private LocalDate departureDate;

    @Column(name = "adult_price", precision = 10, scale = 2)
    private BigDecimal adultPrice;

    @Column(name = "child_price", precision = 10, scale = 2)
    private BigDecimal childPrice;

    @Column(precision = 10, scale = 2)
    private BigDecimal discount;

    @Column(name = "seat_count")
    private Integer seatCount;

    @Column(name = "booked_seats")
    private Integer bookedSeats = 0;

    @OneToMany(mappedBy = "departure")
    private List<BookingTour> bookingTours;
}
