package backend.backend.controller;
import backend.backend.dto.TopTourDTO;
import backend.backend.dto.MyTourBookingDTO;
import backend.backend.dto.StatsDTO;

import backend.backend.entity.ApiResponse;
import backend.backend.entity.TourStatus;
import backend.backend.service.BookingTourService;
import backend.backend.service.TourAdminService;
import backend.backend.utils.ResponseFactory; // <-- Đảm bảo đã import
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus; // <-- Import HttpStatus để sử dụng
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/dashboardTour")
public class DashboardTournController {

    @Autowired
    private TourAdminService tourAdminService;

    @Autowired
    private BookingTourService bookingTourService;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DashboardOverviewDTO {
        private StatsDTO stats;
        private Page<MyTourBookingDTO> bookings;
        private List<TopTourDTO> topTours;
    }

    @GetMapping("/overview")
    public ResponseEntity<ApiResponse<DashboardOverviewDTO>> getDashboardOverview(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Pageable pageable) {

        // ✅ BẮT ĐẦU KIỂM TRA
        System.out.println("==========================================================");
        System.out.println("✅ [DashboardTour] Endpoint /overview được gọi.");
        System.out.println("   - userId nhận được: " + userId);
        System.out.println("   - startDate nhận được: " + startDate);
        System.out.println("   - endDate nhận được: " + endDate);
        System.out.println("==========================================================");


        if (userId == null) {
            System.out.println("❌ [DashboardTour] Lỗi: userId là null. Trả về lỗi 400.");
            return ResponseFactory.error(
                HttpStatus.BAD_REQUEST,
                "Yêu cầu không hợp lệ, vui lòng cung cấp 'userId'."
            );
        }

        // Gọi các service và in kết quả
        StatsDTO stats = bookingTourService.getStatsByDateRange(userId, startDate, endDate);
        System.out.println("   - Kết quả từ getStatsByDateRange: " + stats);
        
        long totalTours = tourAdminService.countTotalTours(userId);
        System.out.println("   - Kết quả từ countTotalTours: " + totalTours);
        
        long activeTours = tourAdminService.countByStatus(userId, TourStatus.ACTIVE);
        System.out.println("   - Kết quả từ countByStatus (ACTIVE): " + activeTours);

        stats.setSystemTotalTours(totalTours);
        stats.setSystemActiveTours(activeTours);
        
        List<TopTourDTO> topTours = bookingTourService.getTopSellingTours(userId, startDate, endDate, 5);
        System.out.println("   - Kết quả từ getTopSellingTours (số lượng): " + (topTours != null ? topTours.size() : "null"));

        Page<MyTourBookingDTO> bookingPage = bookingTourService.getPaidBookingsByDateRange(userId, startDate, endDate, pageable);
        System.out.println("   - Kết quả từ getPaidBookings (số lượng): " + (bookingPage != null ? bookingPage.getNumberOfElements() : "null"));
        
        DashboardOverviewDTO overviewData = DashboardOverviewDTO.builder()
                .stats(stats)
                .bookings(bookingPage)
                .topTours(topTours)
                .build();
        
        System.out.println("✅ [DashboardTour] Dữ liệu đã được tổng hợp. Chuẩn bị trả về response thành công.");
        System.out.println("==========================================================");

        return ResponseFactory.success(overviewData, "Lấy dữ liệu tổng quan thành công.");
    }

    /**
     * @deprecated Endpoint này không hiệu quả. Sử dụng `/overview` để thay thế.
     */
    @Deprecated
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Object>> getDashboardStats_deprecated() {
        // ✅ SỬA LẠI: Dùng ResponseFactory.error để tạo response lỗi
        return ResponseFactory.error(
            HttpStatus.GONE, // Mã lỗi 410: Gone
            "Endpoint này đã cũ. Vui lòng sử dụng /api/v1/dashboardTour/overview."
        );
    }

    /**
     * @deprecated Endpoint này không hiệu quả. Sử dụng `/overview` để thay thế.
     */
    @Deprecated
    @GetMapping("/bookings")
    public ResponseEntity<ApiResponse<Object>> getBookings_deprecated() {
        // ✅ SỬA LẠI: Dùng ResponseFactory.error để tạo response lỗi
        return ResponseFactory.error(
            HttpStatus.GONE, // Mã lỗi 410: Gone
            "Endpoint này đã cũ. Vui lòng sử dụng /api/v1/dashboardTour/overview."
        );
    }
}