package backend.backend.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import lombok.Data;
import java.util.List;

@Data
public class HotelBookingDto {
    private Integer id;
    private String customerName;
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
    private Short rooms;
    private String status;

    public HotelBookingDto(Integer id, String customerName, String hotelName, LocalDateTime createdAt, String status, BigDecimal totalPrice) {
        this.id = id;
        this.customerName = customerName;
        this.hotelName = hotelName;
        this.createdAt = createdAt;
        this.status = status;
        this.totalPrice = totalPrice;
    }

    public HotelBookingDto() {
    }
}
