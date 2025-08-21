package backend.backend.dto.BusDTO;

import java.time.OffsetDateTime;

import backend.backend.entity.BusAmenity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusAmenityResponse {
    private Integer id;
    private String name;
    private String description;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;


    public BusAmenityResponse(BusAmenity amenity) {
        this.id = amenity.getId();
        this.name = amenity.getName();
        this.description = amenity.getDescription();
        this.createdAt = amenity.getCreatedAt();
        this.updatedAt = amenity.getUpdatedAt();
    }
}
