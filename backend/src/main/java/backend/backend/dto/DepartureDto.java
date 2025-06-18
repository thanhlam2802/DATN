package backend.backend.dto;

import backend.backend.entity.Departure;
import lombok.Data;                    

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

@Data 
public class DepartureDto {
    private Integer id;
    private String departureDate; 
    private BigDecimal adultPrice;
    private BigDecimal childPrice;
    private BigDecimal discount;
    private Integer seatCount;
    private Integer bookedSeats;

    public static DepartureDto fromEntity(Departure departure) {
 
        DepartureDto dto = new DepartureDto();
        dto.setId(departure.getId());
        dto.setDepartureDate(departure.getDepartureDate() != null ?
                             departure.getDepartureDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) :
                             null);
        dto.setAdultPrice(departure.getAdultPrice());
        dto.setChildPrice(departure.getChildPrice());
        dto.setDiscount(departure.getDiscount());
        dto.setSeatCount(departure.getSeatCount());
        dto.setBookedSeats(departure.getBookedSeats());

        return dto;
    }
}