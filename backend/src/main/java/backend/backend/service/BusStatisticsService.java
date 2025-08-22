package backend.backend.service;

import backend.backend.dto.bus.BusStatisticsDto;

/**
 * 📊 Service cho Bus Statistics
 * Thống kê xe buýt theo ownerId
 */
public interface BusStatisticsService {

    /**
     * 📈 Lấy thống kê tổng quan cho owner
     * @param ownerId ID của owner
     * @return Thống kê tổng quan
     */
    BusStatisticsDto getOverviewStatistics(Integer ownerId);

    /**
     * 📅 Lấy thống kê theo ngày
     * @param ownerId ID của owner
     * @param date Ngày cần thống kê
     * @return Thống kê ngày
     */
    BusStatisticsDto.DailyStats getDailyStatistics(Integer ownerId, String date);

    /**
     * 📅 Lấy thống kê theo tuần
     * @param ownerId ID của owner
     * @param weekStart Ngày bắt đầu tuần (yyyy-MM-dd)
     * @return Thống kê tuần
     */
    BusStatisticsDto.WeeklyStats getWeeklyStatistics(Integer ownerId, String weekStart);

    /**
     * 📅 Lấy thống kê theo tháng
     * @param ownerId ID của owner
     * @param monthYear Tháng năm (yyyy-MM)
     * @return Thống kê tháng
     */
    BusStatisticsDto.MonthlyStats getMonthlyStatistics(Integer ownerId, String monthYear);

    /**
     * 🛣️ Lấy top tuyến đường phổ biến
     * @param ownerId ID của owner
     * @param limit Số lượng tuyến đường (mặc định: 5)
     * @return Danh sách tuyến đường phổ biến
     */
    java.util.List<BusStatisticsDto.RouteStats> getTopRoutes(Integer ownerId, Integer limit);

    /**
     * 🚌 Lấy thống kê trạng thái xe buýt
     * @param ownerId ID của owner
     * @return Map trạng thái -> số lượng
     */
    java.util.Map<String, Long> getBusStatusStatistics(Integer ownerId);

    /**
     * 📊 Lấy tỷ lệ lấp đầy xe buýt
     * @param ownerId ID của owner
     * @return Map busId -> tỷ lệ lấp đầy
     */
    java.util.Map<String, Double> getOccupancyRates(Integer ownerId);
}
