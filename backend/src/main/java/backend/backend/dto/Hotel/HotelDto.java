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
    private List<AmenityDto> amenities;

    public HotelDto(Integer id, String name, String imageUrl, String address, String provinceName, Short starRating,
            double rating, int reviewCount, BigDecimal startingPrice) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.address = address;
        this.provinceName = provinceName;
        this.starRating = starRating;
        this.rating = rating;
        this.reviewCount = reviewCount;
        this.startingPrice = startingPrice;
    }

    public HotelDto(Integer id, String name, String imageUrl, List<String> imageUrls, String address,
            String provinceName, Short starRating,
            double rating, int reviewCount, BigDecimal startingPrice) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.imageUrls = imageUrls;
        this.address = address;
        this.provinceName = provinceName;
        this.starRating = starRating;
        this.rating = rating;
        this.reviewCount = reviewCount;
        this.startingPrice = startingPrice;
    }
}