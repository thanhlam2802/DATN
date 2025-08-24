package backend.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlightAdminSummaryDto {
    private Integer adminId;
    private String adminName;
    private String adminEmail;
    private int totalFlights;
    private Double totalRevenue;
    private Long totalBookings;
    private Double averageOccupancyRate;
}
