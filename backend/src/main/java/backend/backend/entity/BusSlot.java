// backend.backend.entity.BusSlot.java
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
import java.util.List;

@Data
@Entity
@Table(name = "bus_slots")
public class BusSlot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bus_id", nullable = false)
    private Bus bus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;

    @Column(name = "slot_date", nullable = false)
    private LocalDate slotDate;

    @Column(name = "departure_time", nullable = false)
    private LocalTime departureTime;

    @Column(name = "arrival_time", nullable = false)
    private LocalTime arrivalTime;

    @Column(name = "actual_departure_time") // <-- THÊM TRƯỜNG MỚI
    private LocalDateTime actualDepartureTime;

    @Column(name = "actual_arrival_time") // <-- THÊM TRƯỜNG MỚI
    private LocalDateTime actualArrivalTime;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(name = "total_seats")
    private Integer totalSeats;

    @Column(name = "available_seats")
    private Integer availableSeats;

    @Enumerated(EnumType.STRING) // <-- THAY ĐỔI: Sử dụng EnumType.STRING để lưu enum dưới dạng chuỗi
    @Column(name = "status", length = 50, nullable = false)
    private BusSlotStatus status = BusSlotStatus.SCHEDULED; // <-- THAY ĐỔI: Sử dụng enum

    // --- Real-time Management Fields ---
    @Enumerated(EnumType.STRING) // <-- THÊM TRƯỜNG MỚI
    @Column(name = "delay_reason", length = 50)
    private DelayReason delayReason;

    @Column(name = "current_location", length = 255) // <-- THÊM TRƯỜNG MỚI
    private String currentLocation;

    @Column(name = "driver_contact_info", length = 255) // <-- THÊM TRƯỜNG MỚI
    private String driverContactInfo;

    @Column(name = "allow_manual_override", nullable = false) // <-- THÊM TRƯỜNG MỚI
    private Boolean allowManualOverride = false; // Giá trị mặc định

    @Column(name = "time_tolerance_minutes", nullable = false) // <-- THÊM TRƯỜNG MỚI
    private Integer timeToleranceMinutes = 30; // Giá trị mặc định

    // --- Auto-management Fields ---
    @Enumerated(EnumType.STRING) // <-- THÊM TRƯỜNG MỚI
    @Column(name = "trip_type", length = 20, nullable = false)
    private TripType tripType = TripType.ONE_TIME; // Giá trị mặc định

    @Enumerated(EnumType.STRING) // <-- THÊM TRƯỜNG MỚI
    @Column(name = "recurring_pattern", length = 20)
    private RecurringPattern recurringPattern;

    @Column(name = "recurring_end_date") // <-- THÊM TRƯỜNG MỚI
    private LocalDate recurringEndDate;

    @Column(name = "auto_status_update", nullable = false) // <-- THÊM TRƯỜNG MỚI
    private Boolean autoStatusUpdate = false; // Giá trị mặc định

    @Column(name = "auto_reset_seats", nullable = false) // <-- THÊM TRƯỜNG MỚI
    private Boolean autoResetSeats = false; // Giá trị mặc định

    @OneToMany(mappedBy = "busSlot")
    private List<BusBooking> busBookings;

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