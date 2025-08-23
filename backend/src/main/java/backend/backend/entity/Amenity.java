package backend.backend.entity;

import lombok.Data;
import jakarta.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "amenities")
public class Amenity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 200)
    private String icon;

    @Column(nullable = false, length = 20)
    private String status = "ACTIVE";

    @ManyToMany(mappedBy = "amenities")
    private List<HotelRoom> hotelRooms;
}