package backend.backend.dto.Hotel;

import backend.backend.entity.Amenity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AmenityDto {
    private String name;
    private String icon;

    public static AmenityDto fromEntity(Amenity amenity) {
        return new AmenityDto(amenity.getName(), amenity.getIcon());
    }
}