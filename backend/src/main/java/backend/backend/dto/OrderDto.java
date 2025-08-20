package backend.backend.dto;

import backend.backend.dto.BusDTO.BusBookingDto;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDto {
    private Integer id;
    private Integer userId;
    private BigDecimal amount;
    private BigDecimal originalAmount;
    private String status;
    private LocalDateTime payDate;
    private Integer voucherId;
    private Integer destinationId;
    private LocalDateTime createdAt;
    private String mainProduct;
    private LocalDateTime expiresAt;
    private String transactionId;
    private List<BookingTourDto> tourBookings;
    private List<FlightBookingDto> flightBookings;
    private List<HotelBookingDto> hotelBookings;
    private List<BusBookingDto> busBookings;

}