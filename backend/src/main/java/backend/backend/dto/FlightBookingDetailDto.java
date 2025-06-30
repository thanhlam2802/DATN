package backend.backend.dto;

import java.time.LocalDateTime;
import java.util.List;

public class FlightBookingDetailDto {
    private Long bookingId;
    private FlightDto flight;
    private List<FlightBookingDto.PassengerInfo> passengerInfo;
    private FlightBookingDto.ContactInfo contactInfo;
    private List<String> seatCodes;
    private double totalPrice;
    private String status;
    private LocalDateTime createdAt;

    // Getters and setters
    public Long getBookingId() { return bookingId; }
    public void setBookingId(Long bookingId) { this.bookingId = bookingId; }
    public FlightDto getFlight() { return flight; }
    public void setFlight(FlightDto flight) { this.flight = flight; }
    public List<FlightBookingDto.PassengerInfo> getPassengerInfo() { return passengerInfo; }
    public void setPassengerInfo(List<FlightBookingDto.PassengerInfo> passengerInfo) { this.passengerInfo = passengerInfo; }
    public FlightBookingDto.ContactInfo getContactInfo() { return contactInfo; }
    public void setContactInfo(FlightBookingDto.ContactInfo contactInfo) { this.contactInfo = contactInfo; }
    public List<String> getSeatCodes() { return seatCodes; }
    public void setSeatCodes(List<String> seatCodes) { this.seatCodes = seatCodes; }
    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
} 