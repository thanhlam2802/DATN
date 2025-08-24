/**
 * 📊 Bus Statistics Types
 * JavaScript definitions cho bus statistics
 */

/**
 * @typedef {Object} DailyStats
 * @property {string} date - Ngày thống kê
 * @property {number} trips - Số chuyến đi
 * @property {number} bookings - Số đặt vé
 * @property {number} revenue - Doanh thu
 * @property {number} customers - Số khách hàng
 */

/**
 * @typedef {Object} WeeklyStats
 * @property {string} weekRange - Khoảng thời gian tuần
 * @property {number} trips - Số chuyến đi
 * @property {number} bookings - Số đặt vé
 * @property {number} revenue - Doanh thu
 * @property {number} customers - Số khách hàng
 * @property {DailyStats[]} dailyBreakdown - Thống kê từng ngày
 */

/**
 * @typedef {Object} MonthlyStats
 * @property {string} monthYear - Tháng năm
 * @property {number} trips - Số chuyến đi
 * @property {number} bookings - Số đặt vé
 * @property {number} revenue - Doanh thu
 * @property {number} customers - Số khách hàng
 * @property {DailyStats[]} dailyBreakdown - Thống kê từng ngày
 */

/**
 * @typedef {Object} RouteStats
 * @property {number} routeId - ID tuyến đường
 * @property {string} routeName - Tên tuyến đường
 * @property {number} totalTrips - Tổng số chuyến
 * @property {number} totalBookings - Tổng số đặt vé
 * @property {number} totalRevenue - Tổng doanh thu
 * @property {number} averageOccupancy - Tỷ lệ lấp đầy trung bình
 */

/**
 * @typedef {Object} BusStatisticsDto
 * @property {number} totalBuses - Tổng số xe buýt
 * @property {number} totalTrips - Tổng số chuyến đi
 * @property {number} totalBookings - Tổng số đặt vé
 * @property {number} totalRevenue - Tổng doanh thu
 * @property {number} totalCustomers - Tổng số khách hàng
 * @property {DailyStats} todayStats - Thống kê hôm nay
 * @property {WeeklyStats} weeklyStats - Thống kê tuần
 * @property {MonthlyStats} monthlyStats - Thống kê tháng
 * @property {RouteStats[]} topRoutes - Top tuyến đường
 * @property {Object.<string, number>} busStatusCount - Thống kê trạng thái xe buýt
 * @property {Object.<string, number>} occupancyRates - Tỷ lệ lấp đầy
 */

// Export empty object để có thể import
export default {};
