package backend.backend.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FlightSeatGroupDto {
    private Boolean isBusiness;
    private Boolean isWindow;
    private BigDecimal price;
    private Integer count;
    private Integer carryOnLuggage;
}
