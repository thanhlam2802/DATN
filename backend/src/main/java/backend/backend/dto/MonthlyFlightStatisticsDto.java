package backend.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonthlyFlightStatisticsDto {
    private Integer year;
    private Integer month;
    
    // Thống kê tháng hiện tại
    private Long totalFlightsCurrentMonth;
    private Long totalBookingsCurrentMonth;
    private Double totalRevenueCurrentMonth;
    private Double averageOccupancyRateCurrentMonth;
    
    // Thống kê tháng trước
    private Long totalFlightsPreviousMonth;
    private Long totalBookingsPreviousMonth;
    private Double totalRevenuePreviousMonth;
    private Double averageOccupancyRatePreviousMonth;
    
    // Phần trăm thay đổi so với tháng trước
    private Double flightsChangePercentage;
    private Double bookingsChangePercentage;
    private Double revenueChangePercentage;
    private Double occupancyChangePercentage;
} 