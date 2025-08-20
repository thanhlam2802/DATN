package backend.backend.entity;



import lombok.AllArgsConstructor;
import lombok.Data;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bus_images")
public class BusImage {
    @EmbeddedId
    private BusImageId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("busId")
    @JoinColumn(name = "bus_id")
    @ToString.Include
    private Bus bus;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("imageId")
    @JoinColumn(name = "image_id")
    @ToString.Include
    private Image image;
}