package backend.backend.entity;

import lombok.Data;
import jakarta.persistence.Embeddable;
import java.io.Serializable;

@Data
@Embeddable
public class HotelRoomImageId implements Serializable {
    private Integer roomId;
    private Integer imageId;
} 