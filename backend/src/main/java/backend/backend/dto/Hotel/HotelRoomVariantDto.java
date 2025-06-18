package backend.backend.dto.Hotel;

import backend.backend.entity.HotelRoomVariant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HotelRoomVariantDto {
    private Integer id;
    private String variantName;
    private Boolean hasBreakfast;
    private Boolean cancellable;
    private Boolean payAtHotel;
    private BigDecimal price;

    public static HotelRoomVariantDto fromEntity(HotelRoomVariant variant) {
        return new HotelRoomVariantDto(
                variant.getId(),
                variant.getVariantName(),
                variant.getHasBreakfast(),
                variant.getCancellable(),
                variant.getPayAtHotel(),
                variant.getPrice());
    }
}