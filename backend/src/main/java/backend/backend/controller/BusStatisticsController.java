package backend.backend.controller;

import backend.backend.dto.bus.BusStatisticsDto;
import backend.backend.service.BusStatisticsService;
import backend.backend.utils.ResponseFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 📊 Controller cho Bus Statistics
 * Thống kê xe buýt theo ownerId
 */
@RestController
@RequestMapping("/api/v1/bus/statistics")
@RequiredArgsConstructor
@Slf4j
public class BusStatisticsController {

    private final BusStatisticsService busStatisticsService;

    /**
     * 📈 Lấy thống kê tổng quan cho owner
     * @param ownerId ID của owner
     * @return Thống kê tổng quan
     */
    @GetMapping("/overview/{ownerId}")
    public ResponseEntity<?> getOverviewStatistics(@PathVariable Integer ownerId) {
        try {
            log.info("📊 [Bus Statistics] Getting overview statistics for owner: {}", ownerId);
            
            BusStatisticsDto statistics = busStatisticsService.getOverviewStatistics(ownerId);
            
            log.info("✅ [Bus Statistics] Successfully retrieved overview statistics for owner: {}", ownerId);
            return ResponseFactory.success(statistics, "Thống kê tổng quan đã được tải thành công");
            
        } catch (Exception e) {
            log.error("❌ [Bus Statistics] Error getting overview statistics for owner: {}", ownerId, e);
            return ResponseFactory.error(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR, 
                "Không thể tải thống kê tổng quan: " + e.getMessage());
        }
    }

    /**
     * 📅 Lấy thống kê theo ngày
     * @param ownerId ID của owner
     * @param date Ngày cần thống kê (yyyy-MM-dd)
     * @return Thống kê ngày
     */
    @GetMapping("/daily/{ownerId}")
    public ResponseEntity<?> getDailyStatistics(
            @PathVariable Integer ownerId,
            @RequestParam String date) {
        try {
            log.info("📅 [Bus Statistics] Getting daily statistics for owner: {} on date: {}", ownerId, date);
            
            BusStatisticsDto.DailyStats dailyStats = busStatisticsService.getDailyStatistics(ownerId, date);
            
            log.info("✅ [Bus Statistics] Successfully retrieved daily statistics for owner: {} on date: {}", ownerId, date);
            return ResponseFactory.success(dailyStats, "Thống kê ngày đã được tải thành công");
            
        } catch (Exception e) {
            log.error("❌ [Bus Statistics] Error getting daily statistics for owner: {} on date: {}", ownerId, date, e);
            return ResponseFactory.error(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR, 
                "Không thể tải thống kê ngày: " + e.getMessage());
        }
    }

    /**
     * 📅 Lấy thống kê theo tuần
     * @param ownerId ID của owner
     * @param weekStart Ngày bắt đầu tuần (yyyy-MM-dd)
     * @return Thống kê tuần
     */
    @GetMapping("/weekly/{ownerId}")
    public ResponseEntity<?> getWeeklyStatistics(
            @PathVariable Integer ownerId,
            @RequestParam String weekStart) {
        try {
            log.info("📅 [Bus Statistics] Getting weekly statistics for owner: {} starting: {}", ownerId, weekStart);
            
            BusStatisticsDto.WeeklyStats weeklyStats = busStatisticsService.getWeeklyStatistics(ownerId, weekStart);
            
            log.info("✅ [Bus Statistics] Successfully retrieved weekly statistics for owner: {} starting: {}", ownerId, weekStart);
            return ResponseFactory.success(weeklyStats, "Thống kê tuần đã được tải thành công");
            
        } catch (Exception e) {
            log.error("❌ [Bus Statistics] Error getting weekly statistics for owner: {} starting: {}", ownerId, weekStart, e);
            return ResponseFactory.error(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR, 
                "Không thể tải thống kê tuần: " + e.getMessage());
        }
    }

    /**
     * 📅 Lấy thống kê theo tháng
     * @param ownerId ID của owner
     * @param monthYear Tháng năm (yyyy-MM)
     * @return Thống kê tháng
     */
    @GetMapping("/monthly/{ownerId}")
    public ResponseEntity<?> getMonthlyStatistics(
            @PathVariable Integer ownerId,
            @RequestParam String monthYear) {
        try {
            log.info("📅 [Bus Statistics] Getting monthly statistics for owner: {} for month: {}", ownerId, monthYear);
            
            BusStatisticsDto.MonthlyStats monthlyStats = busStatisticsService.getMonthlyStatistics(ownerId, monthYear);
            
            log.info("✅ [Bus Statistics] Successfully retrieved monthly statistics for owner: {} for month: {}", ownerId, monthYear);
            return ResponseFactory.success(monthlyStats, "Thống kê tháng đã được tải thành công");
            
        } catch (Exception e) {
            log.error("❌ [Bus Statistics] Error getting monthly statistics for owner: {} for month: {}", ownerId, monthYear, e);
            return ResponseFactory.error(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR, 
                "Không thể tải thống kê tháng: " + e.getMessage());
        }
    }

    /**
     * 🛣️ Lấy top tuyến đường phổ biến
     * @param ownerId ID của owner
     * @param limit Số lượng tuyến đường (mặc định: 5)
     * @return Danh sách tuyến đường phổ biến
     */
    @GetMapping("/routes/{ownerId}")
    public ResponseEntity<?> getTopRoutes(
            @PathVariable Integer ownerId,
            @RequestParam(defaultValue = "5") Integer limit) {
        try {
            log.info("🛣️ [Bus Statistics] Getting top routes for owner: {} with limit: {}", ownerId, limit);
            
            List<BusStatisticsDto.RouteStats> topRoutes = busStatisticsService.getTopRoutes(ownerId, limit);
            
            log.info("✅ [Bus Statistics] Successfully retrieved top routes for owner: {} with limit: {}", ownerId, limit);
            return ResponseFactory.success(topRoutes, "Top tuyến đường đã được tải thành công");
            
        } catch (Exception e) {
            log.error("❌ [Bus Statistics] Error getting top routes for owner: {} with limit: {}", ownerId, limit, e);
            return ResponseFactory.error(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR, 
                "Không thể tải top tuyến đường: " + e.getMessage());
        }
    }

    /**
     * 🚌 Lấy thống kê trạng thái xe buýt
     * @param ownerId ID của owner
     * @return Map trạng thái -> số lượng
     */
    @GetMapping("/bus-status/{ownerId}")
    public ResponseEntity<?> getBusStatusStatistics(@PathVariable Integer ownerId) {
        try {
            log.info("🚌 [Bus Statistics] Getting bus status statistics for owner: {}", ownerId);
            
            Map<String, Long> busStatusStats = busStatisticsService.getBusStatusStatistics(ownerId);
            
            log.info("✅ [Bus Statistics] Successfully retrieved bus status statistics for owner: {}", ownerId);
            return ResponseFactory.success(busStatusStats, "Thống kê trạng thái xe buýt đã được tải thành công");
            
        } catch (Exception e) {
            log.error("❌ [Bus Statistics] Error getting bus status statistics for owner: {}", ownerId, e);
            return ResponseFactory.error(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR, 
                "Không thể tải thống kê trạng thái xe buýt: " + e.getMessage());
        }
    }

    /**
     * 📊 Lấy tỷ lệ lấp đầy xe buýt
     * @param ownerId ID của owner
     * @return Map busId -> tỷ lệ lấp đầy
     */
    @GetMapping("/occupancy/{ownerId}")
    public ResponseEntity<?> getOccupancyRates(@PathVariable Integer ownerId) {
        try {
            log.info("📊 [Bus Statistics] Getting occupancy rates for owner: {}", ownerId);
            
            Map<String, Double> occupancyRates = busStatisticsService.getOccupancyRates(ownerId);
            
            log.info("✅ [Bus Statistics] Successfully retrieved occupancy rates for owner: {}", ownerId);
            return ResponseFactory.success(occupancyRates, "Tỷ lệ lấp đầy xe buýt đã được tải thành công");
            
        } catch (Exception e) {
            log.error("❌ [Bus Statistics] Error getting occupancy rates for owner: {}", ownerId, e);
            return ResponseFactory.error(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR, 
                "Không thể tải tỷ lệ lấp đầy xe buýt: " + e.getMessage());
        }
    }
}
