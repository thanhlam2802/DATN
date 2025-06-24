package backend.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TourDto {
    private Long id;
    private String name;
    private String imageUrl;
    private BigDecimal price; 
    private double rating;  
    private int reviewCount;  
    private String location;  
}