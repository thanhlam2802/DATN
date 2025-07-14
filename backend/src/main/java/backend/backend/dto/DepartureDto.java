package backend.backend.dto;

import backend.backend.entity.Departure;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepartureDto {
    private Integer id;
    private String departureDate; 
    private BigDecimal adultPrice;
    private BigDecimal childPrice;
    private BigDecimal discount;
    private Integer seatCount;
    private Integer bookedSeats;

    public static DepartureDto fromEntity(Departure departure) {
        return DepartureDto.builder()
                .id(departure.getId())
                .departureDate(departure.getDepartureDate() != null ?
                             departure.getDepartureDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) :
                             null)
                .adultPrice(departure.getAdultPrice())
                .childPrice(departure.getChildPrice())
                .discount(departure.getDiscount())
                .seatCount(departure.getSeatCount())
                .bookedSeats(departure.getBookedSeats())
                .build();
    }
}