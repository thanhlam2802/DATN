package backend.backend.entity;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import jakarta.persistence.*;

@Data
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
    private String gender;

    @Column(name = "birthday")
    private Date birthday;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "address")
    private String address;

    @Column(name = "password_hash", nullable = false, length = 200)
    private String passwordHash;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

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
    private List<Order> orders;

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