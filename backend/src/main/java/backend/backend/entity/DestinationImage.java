package backend.backend.entity;


import java.io.Serializable;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Embeddable
class DestinationImageId implements Serializable {
    private Integer destinationId;
    private Integer imageId;
}
@Data
@Entity
@Table(name = "destination_images")
public class DestinationImage {
    @EmbeddedId
    private DestinationImageId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("destinationId")
    @JoinColumn(name = "destination_id")
    private TouristDestination destination;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("imageId")
    @JoinColumn(name = "image_id")
    private Image image;
}