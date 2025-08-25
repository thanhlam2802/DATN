package backend.backend.controller.BusController;

import backend.backend.dto.BusDTO.BusBookingResponse;
import backend.backend.dto.BusDTO.DirectBusReservationRequestDto;
import backend.backend.entity.ApiResponse;
import backend.backend.entity.BusBooking;
import backend.backend.service.OrderService;
import backend.backend.service.busService.BusBookingService;
import backend.backend.utils.ResponseFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/bus-booking")
@RequiredArgsConstructor
@CrossOrigin(origins = {
        "https://poly-java-6-fb151.web.app",
        "https://www.travela.io.vn",
        "http://localhost:5173"
})
@Log4j2
public class BusBookingController {

    private final BusBookingService busBookingService;
    private final OrderService  orderService;
    
    @PostMapping("/direct")
    public ResponseEntity<ApiResponse<Integer>> createDirectBooking(
            @RequestBody DirectBusReservationRequestDto directRequest) {
        log.info("Yêu cầu đặt vé xe trực tiếp: busSlotId={}, seats={}", 
                 directRequest.getBusSlotId(), directRequest.getSelectedSeatNumbers());
        try {
            Integer bookingId = orderService.createDirectBusReservation(directRequest);
            log.info("Đặt vé xe trực tiếp thành công: bookingId={}", bookingId);
            return ResponseFactory.created(bookingId, "Giữ chỗ xe thành công. Vui lòng hoàn tất thanh toán trong 30 phút.");
        } catch (Exception e) {
            log.error("Lỗi khi đặt vé xe trực tiếp: {}", e.getMessage());
            throw e;
        }
    }
    /**
     * Get bus booking detail by ID
     */
    @GetMapping("/{bookingId}")
    public ResponseEntity<ApiResponse<BusBookingResponse>> getBookingDetail(@PathVariable Integer bookingId) {
        log.info("Yêu cầu lấy chi tiết đặt vé xe với bookingId={}", bookingId);
        try {
            BusBookingResponse response = busBookingService.getBusBookingDetail(bookingId);
            log.debug("Chi tiết đặt vé xe: status={}, seats={}", 
                     response.getStatus(), response.getSeatNumbers());
            return ResponseFactory.success(response, "Lấy thông tin đặt vé xe thành công.");
        } catch (Exception e) {
            log.error("Lỗi khi lấy chi tiết đặt vé xe: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * ✅ NEW: Get detailed bus booking information for display
     */
    @GetMapping("/{bookingId}/detail")
    public ResponseEntity<ApiResponse<backend.backend.dto.BusDTO.BusBookingDetailDto>> getBookingDetailForDisplay(@PathVariable Integer bookingId) {
        log.info("Yêu cầu lấy thông tin chi tiết vé xe để hiển thị với bookingId={}", bookingId);
        try {
            backend.backend.dto.BusDTO.BusBookingDetailDto detail = busBookingService.getBusBookingDetailForDisplay(bookingId);
            log.debug("Thông tin chi tiết vé xe: route={} → {}, seats={}", 
                     detail.getDepartureLocation(), detail.getArrivalLocation(), detail.getTotalSeats());
            return ResponseFactory.success(detail, "Lấy thông tin chi tiết vé xe thành công.");
        } catch (Exception e) {
            log.error("Lỗi khi lấy thông tin chi tiết vé xe: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Cancel bus booking
     */
    @PostMapping("/{bookingId}/cancel")
    public ResponseEntity<ApiResponse<String>> cancelBooking(@PathVariable Integer bookingId) {
        log.info("Yêu cầu hủy đặt vé xe với bookingId={}", bookingId);
        try {
            busBookingService.cancelBusBooking(bookingId);
            return ResponseFactory.success("OK", "Hủy đặt vé xe thành công.");
        } catch (Exception e) {
            log.error("Lỗi khi hủy đặt vé xe: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * ✅ NEW: Confirm bus booking
     */
    @PostMapping("/{bookingId}/confirm")
    public ResponseEntity<ApiResponse<BusBookingResponse>> confirmBooking(@PathVariable Integer bookingId) {
        log.info("Yêu cầu xác nhận đặt vé xe với bookingId={}", bookingId);
        try {
            BusBooking confirmedBooking = busBookingService.confirmBusBooking(bookingId);
            BusBookingResponse response = busBookingService.getBusBookingDetail(confirmedBooking.getId());
            return ResponseFactory.success(response, "Xác nhận đặt vé xe thành công.");
        } catch (Exception e) {
            log.error("Lỗi khi xác nhận đặt vé xe: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Get customer's bus bookings
     */
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<ApiResponse<List<BusBookingResponse>>> getCustomerBookings(@PathVariable Integer customerId) {
        log.info("Yêu cầu lấy danh sách đặt vé xe của customerId={}", customerId);
        try {
            List<BusBookingResponse> bookings = busBookingService.getCustomerBookings(customerId);
            log.debug("Danh sách đặt vé xe: {} vé", bookings.size());
            return ResponseFactory.success(bookings, "Lấy danh sách đặt vé xe thành công.");
        } catch (Exception e) {
            log.error("Lỗi khi lấy danh sách đặt vé xe: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * ✅ NEW: Get cancellation info
     */
    @GetMapping("/{bookingId}/cancellation-info")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCancellationInfo(@PathVariable Integer bookingId) {
        log.info("Yêu cầu thông tin hủy vé xe với bookingId={}", bookingId);
        try {
            Map<String, Object> info = busBookingService.getCancellationInfo(bookingId);
            return ResponseFactory.success(info, "Lấy thông tin hủy vé thành công.");
        } catch (Exception e) {
            log.error("Lỗi khi lấy thông tin hủy vé: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * ✅ NEW: Request refund
     */
    @PostMapping("/{bookingId}/request-refund")
    public ResponseEntity<ApiResponse<BusBookingResponse>> requestRefund(@PathVariable Integer bookingId) {
        log.info("Yêu cầu hoàn tiền cho bookingId={}", bookingId);
        try {
            BusBookingResponse response = busBookingService.requestRefund(bookingId);
            return ResponseFactory.success(response, "Yêu cầu hoàn tiền đã được gửi thành công.");
        } catch (Exception e) {
            log.error("Lỗi khi yêu cầu hoàn tiền: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * ✅ NEW: Test endpoint for manual cleanup (admin only)
     */
    @PostMapping("/test-cleanup")
    public ResponseEntity<ApiResponse<String>> testCleanup() {
        log.info("Manual cleanup test requested");
        try {
            // Call the cleanup service directly
            busBookingService.releaseExpiredReservations();
            return ResponseFactory.success("OK", "Manual cleanup completed successfully.");
        } catch (Exception e) {
            log.error("Error during manual cleanup: {}", e.getMessage());
            throw e;
        }
    }
}
