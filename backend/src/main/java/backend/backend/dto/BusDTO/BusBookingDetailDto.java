package backend.backend.dto.BusDTO;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Data
public class BusBookingDetailDto {
    // ✅ BASIC BOOKING INFO
    private Integer id;
    private String bookingReference;
    private String status;
    private Integer numPassengers;
    private BigDecimal totalPrice;
    private LocalDateTime bookingDate;
    
    // ✅ ROUTE INFORMATION
    private String departureLocation;
    private String arrivalLocation;
    private LocalDate departureDate;
    private LocalTime departureTime;
    private LocalTime arrivalTime;
    private String tripDuration;
    
    // ✅ BUS INFORMATION
    private String busName;
    private String busLicensePlate;
    private String busCategoryName;
    
    // ✅ SEAT INFORMATION
    private List<SeatInfoDto> selectedSeats;
    private Integer totalSeats;
    
    // ✅ CUSTOMER INFORMATION
    private String customerName;
    private String customerPhone;
    private String customerEmail;
    
    @Data
    public static class SeatInfoDto {
        private Integer id;
        private String seatNumber;
        private String seatType;
        private String seatStatus;
    }
} 