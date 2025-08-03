package backend.backend.controller;

import backend.backend.dto.DashboardTourStatsDTO;
import backend.backend.dto.MyTourBookingDTO;
import backend.backend.dto.TourDetailAdminDTO;
import backend.backend.entity.ApiResponse;
import backend.backend.entity.TourStatus;
import backend.backend.service.BookingTourService;
import backend.backend.service.TourAdminService;
import backend.backend.utils.ResponseFactory;
import lombok.AllArgsConstructor;
import lombok.Data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RestController
@RequestMapping("/api/v1/dashboardTour")
public class DashboardTournController {
	 @Autowired
	    private TourAdminService tourAdminService;

	    @Autowired
	    private BookingTourService bookingTourService;
	    /**
	     * API thống kê, lọc theo khoảng thời gian.
	     * Ví dụ: /stats?startDate=2025-07-01&endDate=2025-07-31
	     */
	    @GetMapping("/stats")
	    public ResponseEntity<ApiResponse<DashboardTourStatsDTO>> getDashboardStats(
	            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
	            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

	        List<MyTourBookingDTO> paidBookings = getPaidBookings();
	        List<MyTourBookingDTO> filteredBookings = filterByDateRange(paidBookings, startDate, endDate);

	        long totalCustomers = filteredBookings.stream().map(MyTourBookingDTO::getEmail).distinct().count();
	        BigDecimal totalRevenue = filteredBookings.stream()
	                .map(MyTourBookingDTO::getTotalPrice)
	                .reduce(BigDecimal.ZERO, BigDecimal::add);

	        long totalTours = tourAdminService.getAllToursForAdmin().size();
	        long activeTours = tourAdminService.getAllToursForAdmin().stream()
	            .filter(tour -> tour.getStatus() == TourStatus.ACTIVE).count();

	        DashboardTourStatsDTO stats = DashboardTourStatsDTO.builder()
	                .totalTours(totalTours)
	                .activeTours(activeTours)
	                .totalCustomers(totalCustomers)
	                .revenueThisMonth(totalRevenue)
	                .build();

	        return ResponseFactory.success(stats, "Lấy dữ liệu thống kê thành công.");
	    }

	    /**
	     * API danh sách booking, lọc theo khoảng thời gian và có phân trang.
	     */
	    @GetMapping("/bookings")
	    public ResponseEntity<ApiResponse<Page<MyTourBookingDTO>>> getBookings(
	            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
	            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
	            Pageable pageable) {

	        List<MyTourBookingDTO> paidBookings = getPaidBookings();
	        List<MyTourBookingDTO> filteredBookings = filterByDateRange(paidBookings, startDate, endDate);

	        int start = (int) pageable.getOffset();
	        int end = Math.min((start + pageable.getPageSize()), filteredBookings.size());
	        Page<MyTourBookingDTO> bookingPage = new PageImpl<>(filteredBookings.subList(start, end), pageable, filteredBookings.size());

	        return ResponseFactory.success(bookingPage, "Lấy danh sách booking thành công.");
	    }

	    // --- HÀM HỖ TRỢ ---
	    private List<MyTourBookingDTO> getPaidBookings() {
	        return bookingTourService.filterAdminBookings(Collections.emptyMap()).stream()
	            .filter(b -> "PAID".equalsIgnoreCase(b.getOrderStatus()) || "CONFIRMED".equalsIgnoreCase(b.getOrderStatus()))
	            .sorted(Comparator.comparing(MyTourBookingDTO::getBookingDate).reversed())
	            .collect(Collectors.toList());
	    }

	    private List<MyTourBookingDTO> filterByDateRange(List<MyTourBookingDTO> bookings, LocalDate startDate, LocalDate endDate) {
	        // Nếu không có bộ lọc, trả về 10 booking gần nhất
	        if (startDate == null && endDate == null) {
	            return bookings.stream().limit(10).collect(Collectors.toList());
	        }
	        
	        final LocalDate finalStartDate = (startDate == null) ? LocalDate.MIN : startDate;
	        final LocalDate finalEndDate = (endDate == null) ? LocalDate.MAX : endDate;

	        return bookings.stream()
	            .filter(b -> !b.getBookingDate().isBefore(finalStartDate) && !b.getBookingDate().isAfter(finalEndDate))
	            .collect(Collectors.toList());
	    }

}
