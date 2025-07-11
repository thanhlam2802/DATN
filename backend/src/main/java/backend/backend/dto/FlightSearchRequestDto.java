package backend.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlightSearchRequestDto {
    private Integer departureAirportId;
    private Integer arrivalAirportId;
    private LocalDate departureDate;
    private LocalDate returnDate;
    private int passengerCount;
    private String seatClass;

} 