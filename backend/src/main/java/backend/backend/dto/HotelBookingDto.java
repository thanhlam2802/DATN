package backend.backend.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import lombok.Data;
import java.util.List;

@Data
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
    private String hotelName;
    private String roomType;
    private String variantName;
    private String imageUrl;
    private List<String> imageUrls;
}
