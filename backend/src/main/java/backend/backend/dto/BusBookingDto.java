package backend.backend.dto;

import java.time.LocalDateTime;
import java.math.BigDecimal;

public class BusBookingDto {
    private Integer id;
    private Integer userId;
    private Integer busSlotId;
    private Integer numPassengers;
    private BigDecimal totalPrice;
    private LocalDateTime bookingDate;
    private Integer orderId;
    // Thêm các trường khác nếu cần
}
