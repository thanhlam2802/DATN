package backend.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FindAvailableSlotRequestDto {
    private Integer flightId;
    private Boolean isAisle;
    private Boolean isWindow;
    private Boolean isBusiness;
} 