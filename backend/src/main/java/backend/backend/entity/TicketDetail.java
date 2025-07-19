package backend.backend.entity;

import java.time.LocalDateTime;
import java.util.List; // Thêm import này

import jakarta.persistence.*; // Thêm import này
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "ticket_details")
public class TicketDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @OneToOne(mappedBy = "ticketDetail", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Order order;

  
    /**
     * Danh sách các vé máy bay trong giỏ hàng.
     * cascade = CascadeType.ALL: Khi lưu/xóa giỏ hàng, các vé máy bay bên trong cũng được xử lý theo.
     * orphanRemoval = true: Nếu một vé máy bay bị xóa khỏi danh sách này, nó cũng sẽ bị xóa khỏi DB.
     */
    @OneToMany(mappedBy = "ticketDetail", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FlightBooking> flightBookings;

    @OneToMany(mappedBy = "ticketDetail", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HotelBooking> hotelBookings;

    @OneToMany(mappedBy = "ticketDetail", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BusBooking> busBookings;

    @OneToMany(mappedBy = "ticketDetail", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookingTour> tourBookings;
    // ==========================================================

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.status = "CART";
    }
}