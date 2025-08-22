package backend.backend.dto.BusDTO;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Data
public class BusBookingDto {
    // ✅ BASIC BOOKING INFO
    private Integer id;
    private Integer userId;
    private Integer busSlotId;
    private Integer numPassengers;
    private BigDecimal totalPrice;
    private LocalDateTime bookingDate;
    private Integer orderId;

    // ✅ BOOKING STATUS & REFERENCE
    private String bookingReference;
    private String status;
    private LocalDateTime expiresAt;
    private String notes;

    // ✅ CUSTOMER INFORMATION
    private Integer customerId;
    private String customerName;
    private String customerPhone;
    private String customerEmail;

    // ✅ BUS SLOT DETAILS
    private LocalDate departureDate;
    private LocalTime departureTime;
    private LocalTime arrivalTime;
    private String busName;
    private String busLicensePlate;
    private String busCategoryName;
    private String departureLocation;
    private String arrivalLocation;

    // ✅ SEAT INFORMATION
    private List<String> seatNumbers;
}
