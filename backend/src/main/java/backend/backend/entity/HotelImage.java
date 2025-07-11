package backend.backend.entity;

import lombok.Data;
import jakarta.persistence.*;

@Data
@Entity
@Table(name = "hotel_images")
public class HotelImage {
    @EmbeddedId
    private HotelImageId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("hotelId")
    @JoinColumn(name = "hotel_id")
    private Hotel hotel;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("imageId")
    @JoinColumn(name = "image_id")
    private Image image;
}