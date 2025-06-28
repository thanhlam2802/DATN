package backend.backend.dto.Hotel;

import backend.backend.entity.Province;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProvinceDto {
    private Integer id;
    private String name;
    private String imageUrl;
    private int hotelCount;

    public static ProvinceDto fromEntity(Province province) {
        return new ProvinceDto(
            province.getId(),
            province.getName(),
            province.getImageUrl(),
            province.getHotels() != null ? province.getHotels().size() : 0 
        );
    }
}