package backend.backend.dto.BusDTO;

import backend.backend.entity.enumBus.BusSeatType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateBusSeatInput {
    private String seatNumber;
    private BigDecimal price;
    private BusSeatType seatType;
    private Integer busSlotId;
}
