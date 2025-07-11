package backend.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlightSlotDto {
    private Integer id;
    private BigDecimal price;
    private Boolean isBusiness;
    private String seatNumber;
    private Boolean isWindow;
    private Boolean isAisle;
    private Integer flightId;
    private Integer carryOnLuggage;
}
