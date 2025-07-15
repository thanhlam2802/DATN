package backend.backend.entity;



import lombok.Data;
import lombok.Getter;
import jakarta.persistence.*;

import backend.backend.entity.TourImageId;


@Getter
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