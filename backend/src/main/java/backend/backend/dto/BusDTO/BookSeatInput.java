package backend.backend.dto.BusDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookSeatInput {
    private Integer seatId;
    private Integer userId;
    private LocalDateTime bookingDate;
}
