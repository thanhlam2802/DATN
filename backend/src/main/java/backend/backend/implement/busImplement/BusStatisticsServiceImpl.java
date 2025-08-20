package backend.backend.implement.busImplement;

import backend.backend.dao.Bus.BusDAO;
import backend.backend.dao.BusBookingDAO;
import backend.backend.dao.Bus.BusSlotDAO;
import backend.backend.dao.Bus.RouteDAO;
import backend.backend.dto.bus.BusStatisticsDto;
import backend.backend.entity.Bus;
import backend.backend.entity.BusBooking;
import backend.backend.entity.BusSlot;
import backend.backend.entity.Route;
import backend.backend.service.BusStatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * 📊 Service Implementation cho Bus Statistics
 * Thống kê xe buýt theo ownerId
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BusStatisticsServiceImpl implements BusStatisticsService {

    private final BusDAO busDAO;
    private final BusSlotDAO busSlotDAO;
    private final BusBookingDAO busBookingDAO;
    private final RouteDAO routeDAO;

    @Override
    public BusStatisticsDto getOverviewStatistics(Integer ownerId) {
        log.info("📊 [Bus Statistics] Getting overview statistics for owner: {}", ownerId);

        try {
            // 📈 Thống kê tổng quan
            Long totalBuses = busDAO.countByOwnerId(ownerId);
            Long totalTrips = busSlotDAO.countByOwnerId(ownerId);
            Long totalBookings = busBookingDAO.countByOwnerId(ownerId);
            BigDecimal totalRevenue = busBookingDAO.getTotalRevenueByOwnerId(ownerId);
            Long totalCustomers = busBookingDAO.countDistinctCustomersByOwnerId(ownerId);

            // 📅 Thống kê hôm nay
            BusStatisticsDto.DailyStats todayStats = getDailyStatistics(ownerId, LocalDate.now().toString());

            // 📅 Thống kê tuần này
            BusStatisticsDto.WeeklyStats weeklyStats = getWeeklyStatistics(ownerId, LocalDate.now().minusDays(6).toString());

            // 📅 Thống kê tháng này
            BusStatisticsDto.MonthlyStats monthlyStats = getMonthlyStatistics(ownerId, LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM")));

            // 🛣️ Top tuyến đường
            List<BusStatisticsDto.RouteStats> topRoutes = getTopRoutes(ownerId, 5);

            // 🚌 Trạng thái xe buýt
            Map<String, Long> busStatusCount = getBusStatusStatistics(ownerId);

            // 📊 Tỷ lệ lấp đầy
            Map<String, Double> occupancyRates = getOccupancyRates(ownerId);

            return BusStatisticsDto.builder()
                    .totalBuses(totalBuses)
                    .totalTrips(totalTrips)
                    .totalBookings(totalBookings)
                    .totalRevenue(totalRevenue != null ? totalRevenue : BigDecimal.ZERO)
                    .totalCustomers(totalCustomers)
                    .todayStats(todayStats)
                    .weeklyStats(weeklyStats)
                    .monthlyStats(monthlyStats)
                    .topRoutes(topRoutes)
                    .busStatusCount(busStatusCount)
                    .occupancyRates(occupancyRates)
                    .build();

        } catch (Exception e) {
            log.error("❌ [Bus Statistics] Error getting overview statistics for owner: {}", ownerId, e);
            throw new RuntimeException("Failed to get overview statistics", e);
        }
    }

    @Override
    public BusStatisticsDto.DailyStats getDailyStatistics(Integer ownerId, String date) {
        log.info("📅 [Bus Statistics] Getting daily statistics for owner: {} on date: {}", ownerId, date);

        try {
            LocalDate targetDate = LocalDate.parse(date);
            LocalDate startOfDay = targetDate;
            LocalDate endOfDay = targetDate;

            // 📊 Thống kê chuyến đi
            Long trips = busSlotDAO.countByOwnerIdAndDateRange(ownerId, startOfDay, endOfDay);

            // 📊 Thống kê đặt vé - cần chuyển đổi LocalDate sang LocalDateTime cho bookingDate
            LocalDateTime startOfDayTime = startOfDay.atStartOfDay();
            LocalDateTime endOfDayTime = endOfDay.atTime(23, 59, 59);
            
            Long bookings = busBookingDAO.countByOwnerIdAndDateRange(ownerId, startOfDayTime, endOfDayTime);

            // 📊 Thống kê doanh thu
            BigDecimal revenue = busBookingDAO.getRevenueByOwnerIdAndDateRange(ownerId, startOfDayTime, endOfDayTime);

            // 📊 Thống kê khách hàng
            Long customers = busBookingDAO.countDistinctCustomersByOwnerIdAndDateRange(ownerId, startOfDayTime, endOfDayTime);

            return BusStatisticsDto.DailyStats.builder()
                    .date(targetDate)
                    .trips(trips)
                    .bookings(bookings)
                    .revenue(revenue != null ? revenue : BigDecimal.ZERO)
                    .customers(customers)
                    .build();

        } catch (Exception e) {
            log.error("❌ [Bus Statistics] Error getting daily statistics for owner: {} on date: {}", ownerId, date, e);
            throw new RuntimeException("Failed to get daily statistics", e);
        }
    }

    @Override
    public BusStatisticsDto.WeeklyStats getWeeklyStatistics(Integer ownerId, String weekStart) {
        log.info("📅 [Bus Statistics] Getting weekly statistics for owner: {} starting: {}", ownerId, weekStart);

        try {
            LocalDate startDate = LocalDate.parse(weekStart);
            LocalDate endDate = startDate.plusDays(6);
            LocalDateTime startOfWeekTime = startDate.atStartOfDay();
            LocalDateTime endOfWeekTime = endDate.atTime(23, 59, 59);

            // 📊 Thống kê tổng tuần
            Long trips = busSlotDAO.countByOwnerIdAndDateRange(ownerId, startDate, endDate);
            Long bookings = busBookingDAO.countByOwnerIdAndDateRange(ownerId, startOfWeekTime, endOfWeekTime);
            BigDecimal revenue = busBookingDAO.getRevenueByOwnerIdAndDateRange(ownerId, startOfWeekTime, endOfWeekTime);
            Long customers = busBookingDAO.countDistinctCustomersByOwnerIdAndDateRange(ownerId, startOfWeekTime, endOfWeekTime);

            // 📊 Thống kê từng ngày trong tuần - Tối ưu hóa: chỉ 2 queries thay vì 28 queries
            List<BusStatisticsDto.DailyStats> dailyBreakdown = new ArrayList<>();
            
            // Lấy thống kê booking theo ngày
            List<Object[]> bookingStats = busBookingDAO.getDailyStatsByDateRange(ownerId, startDate, endDate);
            Map<LocalDate, Map<String, Object>> bookingStatsMap = new HashMap<>();
            
            for (Object[] stat : bookingStats) {
                LocalDate date = (LocalDate) stat[0];
                Map<String, Object> dayStats = new HashMap<>();
                dayStats.put("bookings", stat[1]);
                dayStats.put("revenue", stat[2]);
                dayStats.put("customers", stat[3]);
                bookingStatsMap.put(date, dayStats);
            }
            
            // Lấy thống kê trip theo ngày
            List<Object[]> tripStats = busSlotDAO.getDailyTripStatsByDateRange(ownerId, startDate, endDate);
            Map<LocalDate, Long> tripStatsMap = new HashMap<>();
            
            for (Object[] stat : tripStats) {
                LocalDate date = (LocalDate) stat[0];
                Long tripCount = (Long) stat[1];
                tripStatsMap.put(date, tripCount);
            }
            
            // Tạo daily breakdown
            for (int i = 0; i < 7; i++) {
                LocalDate currentDate = startDate.plusDays(i);
                Map<String, Object> bookingData = bookingStatsMap.get(currentDate);
                Long tripCount = tripStatsMap.get(currentDate);
                
                dailyBreakdown.add(BusStatisticsDto.DailyStats.builder()
                        .date(currentDate)
                        .trips(tripCount != null ? tripCount : 0L)
                        .bookings(bookingData != null ? (Long) bookingData.get("bookings") : 0L)
                        .revenue(bookingData != null && bookingData.get("revenue") != null ? 
                                (BigDecimal) bookingData.get("revenue") : BigDecimal.ZERO)
                        .customers(bookingData != null ? (Long) bookingData.get("customers") : 0L)
                        .build());
            }

            String weekRange = startDate.format(DateTimeFormatter.ofPattern("dd/MM")) + " - " + 
                             endDate.format(DateTimeFormatter.ofPattern("dd/MM"));

            return BusStatisticsDto.WeeklyStats.builder()
                    .weekRange(weekRange)
                    .trips(trips)
                    .bookings(bookings)
                    .revenue(revenue != null ? revenue : BigDecimal.ZERO)
                    .customers(customers)
                    .dailyBreakdown(dailyBreakdown)
                    .build();

        } catch (Exception e) {
            log.error("❌ [Bus Statistics] Error getting weekly statistics for owner: {} starting: {}", ownerId, weekStart, e);
            throw new RuntimeException("Failed to get weekly statistics", e);
        }
    }

    @Override
    public BusStatisticsDto.MonthlyStats getMonthlyStatistics(Integer ownerId, String monthYear) {
        log.info("📅 [Bus Statistics] Getting monthly statistics for owner: {} for month: {}", ownerId, monthYear);

        try {
            LocalDate startDate = LocalDate.parse(monthYear + "-01");
            LocalDate endDate = startDate.plusMonths(1).minusDays(1);
            LocalDateTime startOfMonthTime = startDate.atStartOfDay();
            LocalDateTime endOfMonthTime = endDate.atTime(23, 59, 59);

            // 📊 Thống kê tổng tháng
            Long trips = busSlotDAO.countByOwnerIdAndDateRange(ownerId, startDate, endDate);
            Long bookings = busBookingDAO.countByOwnerIdAndDateRange(ownerId, startOfMonthTime, endOfMonthTime);
            BigDecimal revenue = busBookingDAO.getRevenueByOwnerIdAndDateRange(ownerId, startOfMonthTime, endOfMonthTime);
            Long customers = busBookingDAO.countDistinctCustomersByOwnerIdAndDateRange(ownerId, startOfMonthTime, endOfMonthTime);

            // 📊 Thống kê từng ngày trong tháng - Tối ưu hóa: chỉ 2 queries thay vì 120+ queries
            List<BusStatisticsDto.DailyStats> dailyBreakdown = new ArrayList<>();
            
            // Lấy thống kê booking theo ngày
            List<Object[]> bookingStats = busBookingDAO.getDailyStatsByDateRange(ownerId, startDate, endDate);
            Map<LocalDate, Map<String, Object>> bookingStatsMap = new HashMap<>();
            
            for (Object[] stat : bookingStats) {
                LocalDate date = (LocalDate) stat[0];
                Map<String, Object> dayStats = new HashMap<>();
                dayStats.put("bookings", stat[1]);
                dayStats.put("revenue", stat[2]);
                dayStats.put("customers", stat[3]);
                bookingStatsMap.put(date, dayStats);
            }
            
            // Lấy thống kê trip theo ngày
            List<Object[]> tripStats = busSlotDAO.getDailyTripStatsByDateRange(ownerId, startDate, endDate);
            Map<LocalDate, Long> tripStatsMap = new HashMap<>();
            
            for (Object[] stat : tripStats) {
                LocalDate date = (LocalDate) stat[0];
                Long tripCount = (Long) stat[1];
                tripStatsMap.put(date, tripCount);
            }
            
            // Tạo daily breakdown cho tất cả các ngày trong tháng
            LocalDate currentDate = startDate;
            while (!currentDate.isAfter(endDate)) {
                Map<String, Object> bookingData = bookingStatsMap.get(currentDate);
                Long tripCount = tripStatsMap.get(currentDate);
                
                dailyBreakdown.add(BusStatisticsDto.DailyStats.builder()
                        .date(currentDate)
                        .trips(tripCount != null ? tripCount : 0L)
                        .bookings(bookingData != null ? (Long) bookingData.get("bookings") : 0L)
                        .revenue(bookingData != null && bookingData.get("revenue") != null ? 
                                (BigDecimal) bookingData.get("revenue") : BigDecimal.ZERO)
                        .customers(bookingData != null ? (Long) bookingData.get("customers") : 0L)
                        .build());
                
                currentDate = currentDate.plusDays(1);
            }

            String monthYearStr = startDate.format(DateTimeFormatter.ofPattern("MM/yyyy"));

            return BusStatisticsDto.MonthlyStats.builder()
                    .monthYear(monthYearStr)
                    .trips(trips)
                    .bookings(bookings)
                    .revenue(revenue != null ? revenue : BigDecimal.ZERO)
                    .customers(customers)
                    .dailyBreakdown(dailyBreakdown)
                    .build();

        } catch (Exception e) {
            log.error("❌ [Bus Statistics] Error getting monthly statistics for owner: {} for month: {}", ownerId, monthYear, e);
            throw new RuntimeException("Failed to get monthly statistics", e);
        }
    }

    @Override
    public List<BusStatisticsDto.RouteStats> getTopRoutes(Integer ownerId, Integer limit) {
        log.info("🛣️ [Bus Statistics] Getting top routes for owner: {} with limit: {}", ownerId, limit);

        try {
            List<Route> routes = routeDAO.findByOwnerId(ownerId);
            List<BusStatisticsDto.RouteStats> routeStats = new ArrayList<>();

            for (Route route : routes) {
                Long totalTrips = busSlotDAO.countByRouteId(route.getId());
                Long totalBookings = busBookingDAO.countByRouteId(route.getId());
                BigDecimal totalRevenue = busBookingDAO.getTotalRevenueByRouteId(route.getId());
                Double averageOccupancy = busBookingDAO.getAverageOccupancyByRouteId(route.getId());

                routeStats.add(BusStatisticsDto.RouteStats.builder()
                        .routeId(route.getId())
                        .routeName(route.getOriginLocation().getName() + " → " + route.getDestinationLocation().getName())
                        .totalTrips(totalTrips)
                        .totalBookings(totalBookings)
                        .totalRevenue(totalRevenue != null ? totalRevenue : BigDecimal.ZERO)
                        .averageOccupancy(averageOccupancy != null ? averageOccupancy : 0.0)
                        .build());
            }

            // Sắp xếp theo số chuyến đi giảm dần và giới hạn kết quả
            return routeStats.stream()
                    .sorted((r1, r2) -> r2.getTotalTrips().compareTo(r1.getTotalTrips()))
                    .limit(limit != null ? limit : 5)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("❌ [Bus Statistics] Error getting top routes for owner: {}", ownerId, e);
            throw new RuntimeException("Failed to get top routes", e);
        }
    }

    @Override
    public Map<String, Long> getBusStatusStatistics(Integer ownerId) {
        log.info("🚌 [Bus Statistics] Getting bus status statistics for owner: {}", ownerId);

        try {
            Map<String, Long> statusCount = new HashMap<>();

            // Sử dụng query trực tiếp thay vì lazy loading
            Long activeCount = busDAO.countActiveBusesByOwnerId(ownerId);
            Long inactiveCount = busDAO.countInactiveBusesByOwnerId(ownerId);

            statusCount.put("ACTIVE", activeCount != null ? activeCount : 0L);
            statusCount.put("INACTIVE", inactiveCount != null ? inactiveCount : 0L);
            statusCount.put("MAINTENANCE", 0L); // Tạm thời set 0, có thể thêm logic sau

            return statusCount;

        } catch (Exception e) {
            log.error("❌ [Bus Statistics] Error getting bus status statistics for owner: {}", ownerId, e);
            throw new RuntimeException("Failed to get bus status statistics", e);
        }
    }

    @Override
    public Map<String, Double> getOccupancyRates(Integer ownerId) {
        log.info("📊 [Bus Statistics] Getting occupancy rates for owner: {}", ownerId);

        try {
            // Sử dụng query tối ưu để lấy occupancy rates cho tất cả buses của owner trong một lần query
            Map<String, Double> occupancyRates = busBookingDAO.getAverageOccupancyByOwnerId(ownerId);
            return occupancyRates != null ? occupancyRates : new HashMap<>();

        } catch (Exception e) {
            log.error("❌ [Bus Statistics] Error getting occupancy rates for owner: {}", ownerId, e);
            throw new RuntimeException("Failed to get occupancy rates", e);
        }
    }
}
