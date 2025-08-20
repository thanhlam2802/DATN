package backend.backend.dto.BusDTO;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class BusBookingRequest {
    private Integer busSlotId;
    private Integer customerId;
    private List<Integer> selectedSeatIds;
    private BigDecimal totalPrice;
    private String notes;

    // Passenger info for easy access
    private String passengerName;
    private String passengerPhone;
    private String passengerEmail;
}
