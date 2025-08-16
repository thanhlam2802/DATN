package backend.backend.dto.Hotel;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class UpdateHotelBookingRequestDto {
    private Integer bookingId;
    private Short numAdults;
    private Short numChildren;
    private Short rooms;
    private BigDecimal totalPrice;
} 