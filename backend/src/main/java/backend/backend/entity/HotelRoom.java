package backend.backend.entity;

import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "hotel_rooms")
public class HotelRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    @Column(name = "room_type", length = 100)
    private String roomType;

    @Column(name = "bed_type", length = 100)
    private String bedType;

    @Lob
    private String description;

    @Column(name = "max_adults")
    private Short maxAdults;

    @Column(name = "max_children")
    private Short maxChildren;

    @Column(name = "room_quantity", nullable = false)
    private Short roomQuantity = 1;

    @Column(name = "room_area")
    private Integer roomArea;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @ManyToMany
    @JoinTable(name = "hotel_room_amenities", joinColumns = @JoinColumn(name = "room_id"), inverseJoinColumns = @JoinColumn(name = "amenity_id"))
    private List<Amenity> amenities;

    @OneToMany(mappedBy = "room")
    private List<HotelRoomImage> roomImages;

    @OneToMany(mappedBy = "room")
    private List<HotelRoomVariant> roomVariants;
}