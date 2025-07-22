package backend.backend.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.math.BigDecimal;

public class HotelBookingDto {
    private Integer id;
    private Integer userId;
    private Integer roomVariantId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Short numAdults;
    private Short numChildren;
    private BigDecimal totalPrice;
    private LocalDateTime createdAt;
    private Integer orderId;
    // Thêm các trường khác nếu cần
}
