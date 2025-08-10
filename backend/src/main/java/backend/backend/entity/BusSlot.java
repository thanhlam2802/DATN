package backend.backend.entity;

import backend.backend.entity.enumBus.BusSlotStatus;
import backend.backend.entity.enumBus.DelayReason;
import backend.backend.entity.enumBus.RecurringPattern;
import backend.backend.entity.enumBus.TripType;
import lombok.Data;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime; // Import LocalTime cho thời gian cụ thể
import java.time.LocalDateTime; // Import LocalDateTime cho created_at/updated_at
import java.util.ArrayList;
import java.util.List;

import backend.backend.converter.LocalTimeAttributeConverter; // <-- THÊM IMPORT NÀY

@Data
@Entity
@Table(name = "bus_slots")
public class BusSlot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bus_id", nullable = false)
    private Bus  bus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;

    @Column(name = "slot_date", nullable = false)
    private LocalDate slotDate;

    @Convert(converter = LocalTimeAttributeConverter.class)
    @Column(name = "departure_time", nullable = false)
    private LocalTime departureTime;

    @Convert(converter = LocalTimeAttributeConverter.class)
    @Column(name = "arrival_time", nullable = false)
    private LocalTime arrivalTime;

    @Column(name = "actual_departure_time")
    private LocalDateTime actualDepartureTime;

    @Column(name = "actual_arrival_time")
    private LocalDateTime actualArrivalTime;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(name = "total_seats")
    private Integer totalSeats; // Có thể được suy ra từ số lượng ghế trong collection 'seats'

    @Column(name = "available_seats")
    private Integer availableSeats; // Có thể được suy ra từ số lượng ghế 'isBooked = false' trong collection 'seats'

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 50, nullable = false)
    private BusSlotStatus status = BusSlotStatus.SCHEDULED;

    // --- Real-time Management Fields ---
    @Enumerated(EnumType.STRING)
    @Column(name = "delay_reason", length = 50)
    private DelayReason delayReason;

    @Column(name = "current_location", length = 255)
    private String currentLocation;

    @Column(name = "driver_contact_info", length = 255)
    private String driverContactInfo;

    @Column(name = "allow_manual_override", nullable = false)
    private Boolean allowManualOverride = false;

    @Column(name = "time_tolerance_minutes", nullable = false)
    private Integer timeToleranceMinutes = 30;

    // --- Auto-management Fields ---
    @Enumerated(EnumType.STRING)
    @Column(name = "trip_type", length = 20, nullable = false)
    private TripType tripType = TripType.ONE_TIME;

    @Enumerated(EnumType.STRING)
    @Column(name = "recurring_pattern", length = 20)
    private RecurringPattern recurringPattern;

    @Column(name = "recurring_end_date")
    private LocalDate recurringEndDate;

    @Column(name = "auto_status_update", nullable = false)
    private Boolean autoStatusUpdate = false;

    @Column(name = "auto_reset_seats", nullable = false)
    private Boolean autoResetSeats = false;

    @OneToMany(mappedBy = "busSlot", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BusSeat> seats = new ArrayList<>(); // <-- Đã đổi từ Seat thành BusSeat

    @OneToMany(mappedBy = "busSlot", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BusBooking> busBookings = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
