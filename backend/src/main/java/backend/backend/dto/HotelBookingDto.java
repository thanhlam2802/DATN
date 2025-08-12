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

    public HotelBookingDto(Integer id, String customerName, Integer userId, Integer roomVariantId, LocalDate checkInDate, 
                          LocalDate checkOutDate, Short numAdults, Short numChildren, BigDecimal totalPrice, 
                          LocalDateTime createdAt, Integer orderId, String hotelName, String roomType, 
                          String variantName, String imageUrl, Short rooms, String status) {
        this.id = id;
        this.customerName = customerName;
        this.userId = userId;
        this.roomVariantId = roomVariantId;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.numAdults = numAdults;
        this.numChildren = numChildren;
        this.totalPrice = totalPrice;
        this.createdAt = createdAt;
        this.orderId = orderId;
        this.hotelName = hotelName;
        this.roomType = roomType;
        this.variantName = variantName;
        this.imageUrl = imageUrl;
        this.rooms = rooms;
        this.status = status;
    }

    public HotelBookingDto() {
    }
}
