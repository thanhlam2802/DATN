package backend.backend.dto.Hotel;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HotelApprovalRequestDto {
    private String reason;
}
