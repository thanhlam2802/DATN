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
    private BigDecimal taxAndFeeAmount;

    private String discountType;
    private BigDecimal discountValue;

    public static HotelRoomVariantDto fromEntity(HotelRoomVariant variant) {
        return new HotelRoomVariantDto(
                variant.getId(),
                variant.getVariantName(),
                variant.getHasBreakfast(),
                variant.getCancellable(),
                variant.getPayAtHotel(),
                variant.getPrice(),
                variant.getTaxAndFeeAmount(),
                variant.getDiscountType(),
                variant.getDiscountValue()
        );
    }

    public BigDecimal getTotalPrice() {
        return price.add(taxAndFeeAmount != null ? taxAndFeeAmount : BigDecimal.ZERO);
    }
}