package backend.backend.dto.bus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 📊 DTO cho Bus Statistics
 * Thống kê xe buýt theo ownerId
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusStatisticsDto {

    // 📈 Thống kê tổng quan
    private Long totalBuses;
    private Long totalTrips;
    private Long totalBookings;
    private BigDecimal totalRevenue;
    private Long totalCustomers;

    // 📅 Thống kê theo thời gian
    private DailyStats todayStats;
    private WeeklyStats weeklyStats;
    private MonthlyStats monthlyStats;

    // 🚌 Thống kê chi tiết
    private List<RouteStats> topRoutes;
    private Map<String, Long> busStatusCount;
    private Map<String, Double> occupancyRates;

    // 📊 Thống kê ngày
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyStats {
        private LocalDate date;
        private Long trips;
        private Long bookings;
        private BigDecimal revenue;
        private Long customers;
    }

    // 📊 Thống kê tuần
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WeeklyStats {
        private String weekRange;
        private Long trips;
        private Long bookings;
        private BigDecimal revenue;
        private Long customers;
        private List<DailyStats> dailyBreakdown;
    }

    // 📊 Thống kê tháng
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonthlyStats {
        private String monthYear;
        private Long trips;
        private Long bookings;
        private BigDecimal revenue;
        private Long customers;
        private List<DailyStats> dailyBreakdown;
    }

    // 🛣️ Thống kê tuyến đường
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RouteStats {
        private Integer routeId;
        private String routeName;
        private Long totalTrips;
        private Long totalBookings;
        private BigDecimal totalRevenue;
        private Double averageOccupancy;
    }
}
