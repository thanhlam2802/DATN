package backend.backend.entity;



import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import jakarta.persistence.*;
@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false, unique = true, length = 200)
    private String email;

    @Column(unique = true, length = 20)
    private String phone;

    @Column(name = "password_hash", nullable = false, length = 200)
    private String passwordHash;

    @Lob
    private String address;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

  
    @OneToMany(mappedBy = "user")
    private List<UserRole> userRoles;

    @OneToMany(mappedBy = "user")
    private List<Review> reviews;

    @OneToMany(mappedBy = "user")
    private List<SearchHistory> searchHistories;
    
    @OneToMany(mappedBy = "user")
    private List<Notification> notifications;

    @OneToMany(mappedBy = "owner")
    private List<Hotel> ownedHotels;

    @OneToMany(mappedBy = "owner")
    private List<Flight> ownedFlights;

    @OneToMany(mappedBy = "owner")
    private List<Bus> ownedBuses;

    @OneToMany(mappedBy = "owner")
    private List<Tour> ownedTours;

    @OneToMany(mappedBy = "user")
    private List<UserVoucher> userVouchers;

    @OneToMany(mappedBy = "user")
    private List<TicketBooking> ticketBookings;
    
    @OneToMany(mappedBy = "user")
    private List<Order> orders;

    @OneToMany(mappedBy = "user")
    private List<Payment> payments;

    @OneToMany(mappedBy = "user")
    private List<HotelBooking> hotelBookings;

    @OneToMany(mappedBy = "user")
    private List<BusBooking> busBookings;

    @OneToMany(mappedBy = "user")
    private List<FlightBooking> flightBookings;
}