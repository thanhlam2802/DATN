package backend.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlightBookingDto {
    private Integer id;
    private java.time.LocalDateTime bookingDate;
    private Integer flightSlotId;
    private Integer customerId;
    private Integer flightId;
    private Integer userId;
    private Integer orderId;
    private List<String> seatCodes;
    private double totalPrice;

} 