package backend.backend.entity;



import lombok.Data;
import jakarta.persistence.*;
import java.io.Serializable;

@Data
@Embeddable
class FlightImageId implements Serializable {
    private Integer flightId;
    private Integer imageId;
}

@Data
@Entity
@Table(name = "flight_images")
public class FlightImage {
    @EmbeddedId
    private FlightImageId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("flightId")
    @JoinColumn(name = "flight_id")
    private Flight flight;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("imageId")
    @JoinColumn(name = "image_id")
    private Image image;
}