package backend.backend.controller;

import backend.backend.dto.Hotel.HotelDetailDto;
import backend.backend.entity.ApiResponse;
import backend.backend.service.HotelService;
import backend.backend.utils.ResponseFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Map;
import backend.backend.dto.CustomerDto;
import backend.backend.dao.HotelBookingDAO;
import backend.backend.dto.HotelBookingDto;
import backend.backend.dao.ReviewDAO;
import backend.backend.dao.Hotel.HotelDAO;
import backend.backend.dto.Hotel.*;
import backend.backend.entity.Hotel;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/v1/admin/hotels")
@CrossOrigin(origins = {
        "https://poly-java-6-fb151.web.app",
        "https://www.travela.io.vn",
        "http://localhost:5173"
})
public class HotelAdminController {

    private static final Logger logger = LoggerFactory.getLogger(HotelAdminController.class);

    @Autowired
    private HotelService hotelService;

    @Autowired
    private HotelBookingDAO hotelBookingDAO;

    @Autowired
    private ReviewDAO reviewDAO;
    
    @Autowired
    private HotelDAO hotelDAO;

    @PostMapping
    public ResponseEntity<ApiResponse<HotelDetailDto>> createHotel(MultipartHttpServletRequest request) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String hotelJson = request.getParameter("hotel");
            HotelDetailDto hotelDto = mapper.readValue(hotelJson, HotelDetailDto.class);
            List<MultipartFile> images = request.getFiles("images");
            Map<String, List<MultipartFile>> roomImagesMap = new java.util.HashMap<>();
            java.util.Iterator<String> fileNames = request.getFileNames();
            while (fileNames.hasNext()) {
                String name = fileNames.next();
                if (name.toLowerCase().startsWith("roomimages_")) {
                    String key = name.toLowerCase();
                    List<MultipartFile> files = request.getFiles(name);
                    roomImagesMap.put(key, files);
                }
            }
            HotelDetailDto created = hotelService.createHotel(hotelDto, images, roomImagesMap);
            return ResponseFactory.success(created, "Tạo khách sạn thành công");
        } catch (Exception e) {
            logger.error("[CREATE] Error creating hotel: {}", e.getMessage(), e);
            return ResponseFactory.error(org.springframework.http.HttpStatus.BAD_REQUEST,
                    "Lỗi parse JSON: " + e.getMessage(), null);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<HotelDetailDto>> updateHotel(
            @PathVariable Integer id,
            MultipartHttpServletRequest request) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String hotelJson = request.getParameter("hotel");
            HotelDetailDto hotelDto = mapper.readValue(hotelJson, HotelDetailDto.class);
            List<MultipartFile> images = request.getFiles("images");
            List<String> deleteImageUrls = request.getParameterValues("deleteImageUrls") != null
                    ? java.util.Arrays.asList(request.getParameterValues("deleteImageUrls"))
                    : null;
            Map<String, List<MultipartFile>> roomImagesMap = new java.util.HashMap<>();
            Map<String, List<String>> deleteRoomImageUrlsMap = new java.util.HashMap<>();
            java.util.Iterator<String> fileNames = request.getFileNames();
            while (fileNames.hasNext()) {
                String name = fileNames.next();
                if (name.toLowerCase().startsWith("roomimages_")) {
                    String key = name.toLowerCase();
                    List<MultipartFile> files = request.getFiles(name);
                    roomImagesMap.put(key, files);
                }
            }
            java.util.Enumeration<String> paramNames = request.getParameterNames();
            while (paramNames.hasMoreElements()) {
                String name = paramNames.nextElement();
                if (name.startsWith("deleteRoomImageUrls_")) {
                    String key = name.substring("deleteRoomImageUrls_".length());
                    String[] arr = request.getParameterValues(name);
                    if (arr != null && arr.length > 0) {
                        List<String> urls = new java.util.ArrayList<>();
                        for (String s : arr) {
                            if (s.startsWith("[")) {
                                urls.addAll(java.util.Arrays.asList(mapper.readValue(s, String[].class)));
                            } else {
                                urls.add(s);
                            }
                        }
                        deleteRoomImageUrlsMap.put(key, urls);
                    }
                }
            }
            HotelDetailDto updated = hotelService.updateHotel(id, hotelDto, images, deleteImageUrls, roomImagesMap,
                    deleteRoomImageUrlsMap);
            return ResponseFactory.success(updated, "Cập nhật khách sạn thành công");
        } catch (Exception e) {
            logger.error("[UPDATE] Error updating hotel ID {}: {}", id, e.getMessage(), e);
            return ResponseFactory.error(org.springframework.http.HttpStatus.BAD_REQUEST,
                    "Lỗi parse JSON: " + e.getMessage(), null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteHotel(@PathVariable Integer id) {
        hotelService.deleteHotel(id);
        return ResponseFactory.success(null, "Xóa khách sạn thành công");
    }

    @GetMapping("/{hotelId}/customers")
    public ResponseEntity<ApiResponse<List<CustomerDto>>> getHotelCustomers(@PathVariable Integer hotelId) {
        List<CustomerDto> customers = hotelBookingDAO.findAllCustomersByHotelId(hotelId);
        return ResponseFactory.success(customers, "Lấy danh sách khách hàng thành công");
    }

    @GetMapping("/customers")
    public ResponseEntity<ApiResponse<List<CustomerDto>>> getAllHotelCustomers() {
        List<CustomerDto> customers = hotelBookingDAO.findAllHotelCustomers();
        return ResponseFactory.success(customers, "Lấy danh sách khách hàng thành công");
    }

    @GetMapping("/customers/{customerId}/booked-rooms")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getCustomerBookedRooms(@PathVariable Integer customerId) {
        List<Map<String, Object>> result = hotelBookingDAO.findAllBookingsByCustomerId(customerId);
        return ResponseFactory.success(result, "Lấy lịch sử đặt phòng thành công");
    }

    @GetMapping("/bookings")
    public ResponseEntity<ApiResponse<List<HotelBookingDto>>> getAllHotelBookings() {
        List<HotelBookingDto> bookings = hotelBookingDAO.findAllHotelBookings();
        return ResponseFactory.success(bookings, "Lấy danh sách booking thành công");
    }

    @GetMapping("/dashboard-statistics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDashboardStatistics(
            @RequestParam(defaultValue = "this_month") String timePeriod) {
        Map<String, Object> stats = new java.util.HashMap<>();

        Long currentBookings = hotelBookingDAO.countTotalBookingsByPeriod(timePeriod);
        java.math.BigDecimal currentRevenue = hotelBookingDAO.sumTotalRevenueByPeriod(timePeriod);
        Long currentCustomers = hotelBookingDAO.countTotalCustomersByPeriod(timePeriod);
        Long currentReviews = reviewDAO.countTotalHotelReviewsByPeriod(timePeriod);

        String comparisonPeriod = getComparisonPeriod(timePeriod);
        Long comparisonBookings = hotelBookingDAO.countTotalBookingsByPeriod(comparisonPeriod);
        java.math.BigDecimal comparisonRevenue = hotelBookingDAO.sumTotalRevenueByPeriod(comparisonPeriod);
        Long comparisonCustomers = hotelBookingDAO.countTotalCustomersByPeriod(comparisonPeriod);
        Long comparisonReviews = reviewDAO.countTotalHotelReviewsByPeriod(comparisonPeriod);

        double bookingGrowth = comparisonBookings > 0 ? ((currentBookings - comparisonBookings) * 100.0 / comparisonBookings) : 0;
        double revenueGrowth = comparisonRevenue.compareTo(java.math.BigDecimal.ZERO) > 0 ?
            (currentRevenue.subtract(comparisonRevenue).multiply(java.math.BigDecimal.valueOf(100)).divide(comparisonRevenue, 2, java.math.RoundingMode.HALF_UP)).doubleValue() : 0;
        double customerGrowth = comparisonCustomers > 0 ? ((currentCustomers - comparisonCustomers) * 100.0 / comparisonCustomers) : 0;
        double reviewGrowth = comparisonReviews > 0 ? ((currentReviews - comparisonReviews) * 100.0 / comparisonReviews) : 0;

        stats.put("totalBookings", currentBookings);
        stats.put("totalRevenue", currentRevenue);
        stats.put("totalCustomers", currentCustomers);
        stats.put("totalReviews", currentReviews);

        stats.put("bookingGrowth", Math.round(bookingGrowth * 10.0) / 10.0);
        stats.put("revenueGrowth", Math.round(revenueGrowth * 10.0) / 10.0);
        stats.put("customerGrowth", Math.round(customerGrowth * 10.0) / 10.0);
        stats.put("reviewGrowth", Math.round(reviewGrowth * 10.0) / 10.0);

        return ResponseFactory.success(stats, "Lấy thống kê dashboard thành công");
    }

    @GetMapping("/revenue-chart")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getRevenueChart(
            @RequestParam(defaultValue = "this_month") String timePeriod) {
        try {
            System.out.println("=== REVENUE CHART DEBUG ===");
            System.out.println("Time period requested: " + timePeriod);
            
            Map<String, Object> chartData = hotelBookingDAO.getHotelRevenueByDayChartData(timePeriod);
            
            System.out.println("Chart data returned: " + chartData);
            System.out.println("Labels: " + chartData.get("labels"));
            System.out.println("Datasets: " + chartData.get("datasets"));
            
            return ResponseFactory.success(chartData, "Lấy dữ liệu biểu đồ doanh thu thành công");
        } catch (Exception e) {
            System.err.println("Error in getRevenueChart: " + e.getMessage());
            e.printStackTrace();
            return ResponseFactory.error(HttpStatus.INTERNAL_SERVER_ERROR, "Lỗi khi lấy dữ liệu biểu đồ doanh thu: " + e.getMessage());
        }
    }

    @GetMapping("/revenue-pie-chart")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getRevenuePieChart(
            @RequestParam(defaultValue = "this_month") String timePeriod) {
        try {
            System.out.println("=== REVENUE PIE CHART DEBUG ===");
            System.out.println("Time period requested: " + timePeriod);
            
            Map<String, Object> chartData = hotelBookingDAO.getHotelRevenuePieChartData(timePeriod);
            
            System.out.println("Pie chart data returned: " + chartData);
            System.out.println("Labels: " + chartData.get("labels"));
            System.out.println("Data: " + chartData.get("data"));
            
            return ResponseFactory.success(chartData, "Lấy dữ liệu biểu đồ tròn doanh thu thành công");
        } catch (Exception e) {
            System.err.println("Error in getRevenuePieChart: " + e.getMessage());
            e.printStackTrace();
            return ResponseFactory.error(HttpStatus.INTERNAL_SERVER_ERROR, "Lỗi khi lấy dữ liệu biểu đồ tròn doanh thu: " + e.getMessage());
        }
    }

    @GetMapping("/top-rooms-chart")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getTopRoomsChart(
            @RequestParam(defaultValue = "this_month") String timePeriod) {
        try {
            logger.info("[TOP ROOMS CHART] Getting top rooms chart data for period: {}", timePeriod);
            Map<String, Object> chartData = hotelBookingDAO.getTopRoomsChartData(timePeriod);
            logger.info("[TOP ROOMS CHART] Returning {} rooms", chartData.get("labels"));
            return ResponseFactory.success(chartData, "Lấy dữ liệu biểu đồ top rooms thành công");
        } catch (Exception e) {
            logger.error("[TOP ROOMS CHART] Error getting top rooms chart data: {}", e.getMessage(), e);
            return ResponseFactory.error(HttpStatus.INTERNAL_SERVER_ERROR, "Lỗi khi lấy dữ liệu biểu đồ top rooms", null);
        }
    }

    @GetMapping("/test-data")
    public ResponseEntity<ApiResponse<Map<String, Object>>> testData() {
        try {
            System.out.println("=== TEST DATA DEBUG ===");
            
            Long totalBookings = hotelBookingDAO.count();
            System.out.println("Total bookings: " + totalBookings);
            
            Long todayBookings = hotelBookingDAO.countTotalBookingsToday();
            System.out.println("Today bookings: " + todayBookings);
            
            Long thisMonthBookings = hotelBookingDAO.countTotalBookingsThisMonth();
            System.out.println("This month bookings: " + thisMonthBookings);
            
            java.math.BigDecimal todayRevenue = hotelBookingDAO.sumTotalRevenueToday();
            System.out.println("Today revenue: " + todayRevenue);
            
            Map<String, Object> testData = new java.util.HashMap<>();
            testData.put("totalBookings", totalBookings);
            testData.put("todayBookings", todayBookings);
            testData.put("thisMonthBookings", thisMonthBookings);
            testData.put("todayRevenue", todayRevenue);
            
            return ResponseFactory.success(testData, "Test data retrieved successfully");
        } catch (Exception e) {
            System.err.println("Error in testData: " + e.getMessage());
            e.printStackTrace();
            return ResponseFactory.error(HttpStatus.INTERNAL_SERVER_ERROR, "Error: " + e.getMessage());
        }
    }

    @GetMapping("/reviews")
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<List<AdminReviewDto>>> getAllHotelReviews() {
        try {
            logger.info("[REVIEWS] Starting to fetch hotel reviews");
            
            List<backend.backend.entity.Review> reviews = reviewDAO.findByEntityTypeWithUser("Hotel");
            logger.info("[REVIEWS] Found {} reviews in database", reviews.size());
            
            List<AdminReviewDto> reviewDtos = reviews.stream().map(review -> {
                AdminReviewDto dto = new AdminReviewDto();
                dto.setId(review.getId());
                dto.setRating(review.getRating());
                dto.setContent(review.getContent());
                dto.setCreatedAt(review.getCreatedAt());
                dto.setUpdatedAt(review.getCreatedAt());
                
                if (review.getUser() != null) {
                    String userName = review.getUser().getName();
                    String userEmail = review.getUser().getEmail();
                    dto.setCustomerName(userName != null && !userName.trim().isEmpty() ? userName : "Anonymous");
                    dto.setCustomerEmail(userEmail != null ? userEmail : "N/A");
                } else {
                    dto.setCustomerName("Anonymous");
                    dto.setCustomerEmail("N/A");
                }

                try {
                    Hotel hotel = hotelDAO.findById(review.getEntityId()).orElse(null);
                    if (hotel != null) {
                        dto.setHotelName(hotel.getName());
                        dto.setHotelAddress(hotel.getAddress() != null ? hotel.getAddress() : "Địa chỉ không có");
                    } else {
                        dto.setHotelName("Khách sạn không tồn tại");
                        dto.setHotelAddress("N/A");
                    }
                } catch (Exception e) {
                    logger.warn("Could not fetch hotel details for hotel ID {}: {}", review.getEntityId(), e.getMessage());
                    dto.setHotelName("Hotel " + review.getEntityId());
                    dto.setHotelAddress("Address " + review.getEntityId());
                }
                
                return dto;
            }).collect(java.util.stream.Collectors.toList());
            
            logger.info("[REVIEWS] Returning {} reviews", reviewDtos.size());
            return ResponseFactory.success(reviewDtos, "Lấy danh sách đánh giá thành công");
        } catch (Exception e) {
            logger.error("[REVIEWS] Error getting all hotel reviews: {}", e.getMessage(), e);
            return ResponseFactory.error(HttpStatus.INTERNAL_SERVER_ERROR, "Lỗi khi lấy danh sách đánh giá: " + e.getMessage(), null);
        }
    }

    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<ApiResponse<Void>> deleteHotelReview(@PathVariable Integer reviewId) {
        try {
            if (!reviewDAO.existsById(reviewId)) {
                return ResponseFactory.error(HttpStatus.NOT_FOUND, "Không tìm thấy đánh giá", null);
            }
            
            reviewDAO.deleteById(reviewId);
            return ResponseFactory.success(null, "Xóa đánh giá thành công");
        } catch (Exception e) {
            logger.error("[REVIEWS] Error deleting review {}: {}", reviewId, e.getMessage(), e);
            return ResponseFactory.error(HttpStatus.INTERNAL_SERVER_ERROR, "Lỗi khi xóa đánh giá", null);
        }
    }

    private String getComparisonPeriod(String timePeriod) {
        switch (timePeriod) {
            case "today":
                return "yesterday";
            case "yesterday":
                return "2_days_ago";
            case "last_7_days":
                return "previous_7_days";
            case "this_month":
                return "last_month";
            case "last_month":
                return "2_months_ago";
            default:
                return "last_month";
        }
    }
}