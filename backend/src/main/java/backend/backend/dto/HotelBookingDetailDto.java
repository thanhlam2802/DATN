package backend.backend.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class HotelBookingDetailDto {
    private Integer id;
    private String customerName;
    private String customerPhone;
    private String customerEmail;
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
    private Short rooms;
    private String status;
    private String paymentMethod;

    public HotelBookingDetailDto() {
    }

    public HotelBookingDetailDto(Integer id, String customerName, String customerPhone, String customerEmail,
                               Integer userId, Integer roomVariantId, LocalDate checkInDate, LocalDate checkOutDate,
                               Short numAdults, Short numChildren, BigDecimal totalPrice, LocalDateTime createdAt,
                               Integer orderId, String hotelName, String roomType, String variantName,
                               String imageUrl, Short rooms, String status, String paymentMethod) {
        this.id = id;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.customerEmail = customerEmail;
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
        this.paymentMethod = paymentMethod;
    }
} 