package backend.backend.entity;

import lombok.Data;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "flight_slots")
public class FlightSlot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "price", nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(name = "is_business", nullable = false)
    private Boolean isBusiness;

    @Column(name = "seatNumber", length = 1)
    private String seatNumber;

    @Column(name = "isWindow")
    private Boolean isWindow;

    @Column(name = "isAisle")
    private Boolean isAisle;

    @Column(name = "carry_on_luggage")
    private Integer carryOnLuggage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flight_id", nullable = false)
    private Flight flight;
}
