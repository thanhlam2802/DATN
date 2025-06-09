package backend.backend.entity;



import lombok.Data;
import jakarta.persistence.*;
import java.io.Serializable;

@Data
@Embeddable
class TourImageId implements Serializable {
    private Integer tourId;
    private Integer imageId;
}

@Data
@Entity
@Table(name = "tour_images")
public class TourImage {
    @EmbeddedId
    private TourImageId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("tourId")
    @JoinColumn(name = "tour_id")
    private Tour tour;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("imageId")
    @JoinColumn(name = "image_id")
    private Image image;
}