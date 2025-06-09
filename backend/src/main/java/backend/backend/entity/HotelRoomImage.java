package backend.backend.entity;



import lombok.Data;
import jakarta.persistence.*;
import java.io.Serializable;

@Data
@Embeddable
class HotelRoomImageId implements Serializable {
    private Integer roomId;
    private Integer imageId;
}

@Data
@Entity
@Table(name = "hotel_room_images")
public class HotelRoomImage {
    @EmbeddedId
    private HotelRoomImageId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("roomId")
    @JoinColumn(name = "room_id")
    private HotelRoom room;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("imageId")
    @JoinColumn(name = "image_id")
    private Image image;
}