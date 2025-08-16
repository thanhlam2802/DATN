package backend.backend.entity;

import backend.backend.dto.GenderEnum;
import backend.backend.dto.auth.AuthProvider;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import jakarta.persistence.*;

@Data
@Getter
@Setter
@Entity

@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "email", nullable = false, unique = true, length = 200)
    private String email;

    @Column(name = "gender")
    @Enumerated(EnumType.STRING)
    private GenderEnum gender;

    @Column(name = "birthday")
    private Date birthday;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "address")
    private String address;

    @Column(name = "password_hash", nullable = false, length = 200)
    private String passwordHash;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(name = "is_verified")
    private Boolean isVerified;

    @Column(name = "auth_provider")
    @Enumerated(EnumType.STRING)
    private AuthProvider authProvider;

    @ToString.Exclude
    @OneToMany(mappedBy = "user",fetch = FetchType.EAGER)
    private List<UserRole> userRoles;
    @ToString.Exclude
    @OneToMany(mappedBy = "user")
    private List<Review> reviews;
    @ToString.Exclude
    @OneToMany(mappedBy = "user")
    private List<SearchHistory> searchHistories;
    @ToString.Exclude
    @OneToMany(mappedBy = "user")
    private List<Notification> notifications;
    @ToString.Exclude
    @OneToMany(mappedBy = "owner")
    private List<Hotel> ownedHotels;
    @ToString.Exclude
    @OneToMany(mappedBy = "owner")
    private List<Flight> ownedFlights;
    @ToString.Exclude
    @OneToMany(mappedBy = "owner")
    private List<Bus> ownedBuses;
    @ToString.Exclude
    @OneToMany(mappedBy = "owner")
    private List<Tour> ownedTours;
    @ToString.Exclude
    @OneToMany(mappedBy = "user")
    private List<UserVoucher> userVouchers;

    @ToString.Exclude
    @OneToMany(mappedBy = "user")
    private List<Order> orders;
    @ToString.Exclude
    @OneToMany(mappedBy = "user")
    private List<Payment> payments;

//
//    @OneToMany(mappedBy = "user")
//    private List<HotelBooking> hotelBookings;
//
//    @OneToMany(mappedBy = "user")
//    private List<BusBooking> busBookings;
//
//    @OneToMany(mappedBy = "user")
//    private List<FlightBooking> flightBookings;
}