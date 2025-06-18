package backend.backend.dto.Hotel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HotelDto {
    private Integer id;
    private String name;
    private String imageUrl;
    private String address;
    private String provinceName;
    private Short starRating;
    private double rating;
    private int reviewCount;
    private BigDecimal startingPrice;
}