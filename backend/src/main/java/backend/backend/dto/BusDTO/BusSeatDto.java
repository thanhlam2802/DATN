package backend.backend.dto.BusDTO;

import backend.backend.entity.enumBus.BusSeatType;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class BusSeatDto {
    private Integer id;
    private String seatNumber;
    private BigDecimal price;
    private BusSeatType seatType;
    private Boolean isBooked;
}
