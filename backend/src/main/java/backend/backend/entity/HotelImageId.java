package backend.backend.entity;

import lombok.Data;
import jakarta.persistence.Embeddable;
import java.io.Serializable;

@Data
@Embeddable
public class HotelImageId implements Serializable {
    private Integer hotelId;
    private Integer imageId;
} 