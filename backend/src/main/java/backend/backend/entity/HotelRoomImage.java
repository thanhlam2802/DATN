package backend.backend.entity;

import lombok.Data;
import jakarta.persistence.*;

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