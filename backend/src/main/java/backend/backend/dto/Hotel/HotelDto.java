package backend.backend.dto.Hotel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HotelDto {
    private Integer id;
    private String name;
    private String imageUrl;
    private List<String> imageUrls;
    private String address;
    private String provinceName;
    private Short starRating;
    private double rating;
    private int reviewCount;
    private BigDecimal startingPrice;
    private BigDecimal minDiscountedPrice;
    private List<AmenityDto> amenities;
    private java.time.LocalDateTime createdAt;
    private java.time.LocalDateTime updatedAt;
    
    private String approvalStatus;
    private String approvalReason;
    private java.time.LocalDateTime approvedAt;
    private String approvedBy;
    
    private String status;
}