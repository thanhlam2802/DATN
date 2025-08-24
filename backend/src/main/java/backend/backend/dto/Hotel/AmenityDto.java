package backend.backend.dto.Hotel;

import backend.backend.entity.Amenity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AmenityDto {
    private Integer id;
    private String name;
    private String icon;
    private String status;

    public static AmenityDto fromEntity(Amenity amenity) {
        if (amenity == null) {
            return null;
        }
        return new AmenityDto(amenity.getId(), amenity.getName(), amenity.getIcon(), amenity.getStatus());
    }
}