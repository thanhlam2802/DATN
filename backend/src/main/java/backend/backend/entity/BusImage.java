package backend.backend.entity;



import lombok.Data;
import jakarta.persistence.*;
import java.io.Serializable;

@Data
@Embeddable
class BusImageId implements Serializable {
    private Integer busId;
    private Integer imageId;
}

@Data
@Entity
@Table(name = "bus_images")
public class BusImage {
    @EmbeddedId
    private BusImageId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("busId")
    @JoinColumn(name = "bus_id")
    private Bus bus;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("imageId")
    @JoinColumn(name = "image_id")
    private Image image;
}