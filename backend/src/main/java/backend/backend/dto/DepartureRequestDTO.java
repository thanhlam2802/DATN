package backend.backend.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class DepartureRequestDTO {
    private LocalDate departureDate;
    private BigDecimal adultPrice;
    private BigDecimal childPrice;
    private BigDecimal discount;
    private Integer seatCount;
}