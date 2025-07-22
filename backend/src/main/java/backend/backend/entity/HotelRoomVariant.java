package backend.backend.entity;



import lombok.Data;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "hotel_room_variants")
public class HotelRoomVariant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private HotelRoom room;

    @Column(name = "variant_name", nullable = false, length = 200)
    private String variantName;

    @Column(name = "has_breakfast")
    private Boolean hasBreakfast;

    private Boolean cancellable;

    @Column(name = "pay_at_hotel")
    private Boolean payAtHotel;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(length = 50)
    private String status = "available";

    @Column(name = "tax_and_fee_amount", nullable = true, precision = 12, scale = 2)
    private BigDecimal taxAndFeeAmount;

    @Column(name = "discount_type", length = 10)
    private String discountType;

    @Column(name = "discount_value", precision = 15, scale = 2)
    private BigDecimal discountValue;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @OneToMany(mappedBy = "roomVariant")
    private List<HotelBooking> hotelBookings;
}