package backend.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlightAdminDetailDto {
    private Integer adminId;
    private String adminName;
    private String adminEmail;
    private int totalFlights;
    private Double totalRevenue;
    private Long totalBookings;
    private Double averageOccupancyRate;
    private List<FlightSummaryDto> recentFlights;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FlightSummaryDto {
        private Integer flightId;
        private String flightNumber;
        private String departureAirport;
        private String arrivalAirport;
        private String departureTime;
        private int totalSeats;
        private int bookedSeats;
        private Double revenue;
    }
}
