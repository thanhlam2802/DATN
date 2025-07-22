package backend.backend.dto.BusDTO;

import backend.backend.entity.enumBus.DelayReason;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateActualTimesRequest {
    private OffsetDateTime actualDepartureTime;
    private OffsetDateTime actualArrivalTime;
    private DelayReason delayReason;
    private String currentLocation;
    private String driverNotes;
} 