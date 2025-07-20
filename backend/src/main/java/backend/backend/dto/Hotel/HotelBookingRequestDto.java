package backend.backend.dto.Hotel;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class HotelBookingRequestDto {
    private String fullName;
    private String email;
    private String phone;
    private Integer roomVariantId;
    private String checkInDate;
    private String checkOutDate;
    private Short numAdults;
    private Short numChildren;
    private BigDecimal totalPrice;
} 