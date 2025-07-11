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
public class FlightStatisticsDto {
    private Integer flightId;
    private String flightCode;
    private LocalDate date;
    private int totalTickets;
    private int soldTickets;
    private double revenue;
    private double occupancyRate;

} 