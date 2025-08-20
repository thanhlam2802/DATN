package backend.backend.dto.BusDTO;

import backend.backend.entity.enumBus.BusBookingStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
public class BusBookingResponse {
    
    // ===================================================================
    // BASIC BOOKING INFO
    // ===================================================================
    
    private Integer id;
    private String bookingReference;
    private BusBookingStatus status; // Keep as enum for type safety
    private Integer numPassengers;
    private BigDecimal totalPrice;
    private LocalDateTime bookingDate;
    private LocalDateTime expiresAt;
    private String notes;
    private Integer orderId; // Link to order if part of cart

    // ===================================================================
    // CUSTOMER INFO
    // ===================================================================
    
    private Integer customerId;
    private String customerName;
    private String customerPhone;
    private String customerEmail;

    // ===================================================================
    // BUS SLOT INFO
    // ===================================================================
    
    private Integer busSlotId;
    private LocalDate departureDate;
    private LocalTime departureTime;
    private LocalTime arrivalTime;
    
    // Bus info
    private String busName;
    private String busLicensePlate;
    
    // Route info
    private String departureLocation;
    private String arrivalLocation;

    // ===================================================================
    // SEAT INFO
    // ===================================================================
    
    private List<String> seatNumbers; // Simple list of seat numbers
    
    // ===================================================================
    // NESTED OBJECTS (Optional - for detailed responses)
    // ===================================================================
    
    private BusSlotSummaryDto busSlot;
    private CustomerSummaryDto customer;
    private List<BusSeatDto> selectedSeats;

    // ===================================================================
    // HELPER METHODS
    // ===================================================================
    
    /**
     * Get status as string for frontend compatibility
     */
    public String getStatusName() {
        return status != null ? status.name() : null;
    }
    
    /**
     * Check if booking is active (not cancelled/expired)
     */
    public boolean isActive() {
        return status != null && 
               status != BusBookingStatus.CANCELLED && 
               status != BusBookingStatus.EXPIRED;
    }
    
    /**
     * Check if booking has expired
     */
    public boolean isExpired() {
        return expiresAt != null && expiresAt.isBefore(LocalDateTime.now());
    }
    
    /**
     * Get simple seat count
     */
    public int getSeatCount() {
        return seatNumbers != null ? seatNumbers.size() : numPassengers;
    }
}
