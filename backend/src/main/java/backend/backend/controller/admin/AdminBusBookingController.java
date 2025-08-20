package backend.backend.controller.admin;

import backend.backend.dto.BusDTO.BusBookingResponse;
import backend.backend.entity.ApiResponse;
import backend.backend.service.busService.BusBookingService;
import backend.backend.utils.ResponseFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/bus-booking")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Log4j2
public class AdminBusBookingController {

    private final BusBookingService busBookingService;

    /**
     * ✅ Bulk cancel multiple bookings with reason (void return)
     */
    @PostMapping("/bulk-cancel")
    public ResponseEntity<ApiResponse<String>> bulkCancelBookings(
            @RequestBody Map<String, Object> request) {
        
        @SuppressWarnings("unchecked")
        List<Integer> bookingIds = (List<Integer>) request.get("bookingIds");
        String reason = (String) request.get("reason");
        
        log.info("Admin bulk cancelling {} bookings with reason: {}", bookingIds.size(), reason);
        
        try {
            busBookingService.cancelMultipleBookingsWithReason(bookingIds, reason);
            return ResponseFactory.success("OK", 
                "Đã hủy thành công " + bookingIds.size() + " đặt vé xe.");
        } catch (Exception e) {
            log.error("Error in bulk cancel: {}", e.getMessage());
            return ResponseFactory.error(null, "Lỗi hủy vé: " + e.getMessage(), null);
        }
    }

    /**
     * ✅ Bulk cancel multiple bookings and return detailed results
     */
    @PostMapping("/bulk-cancel-detailed")
    public ResponseEntity<ApiResponse<List<BusBookingResponse>>> bulkCancelBookingsDetailed(
            @RequestBody Map<String, Object> request) {
        
        @SuppressWarnings("unchecked")
        List<Integer> bookingIds = (List<Integer>) request.get("bookingIds");
        
        log.info("Admin bulk cancelling {} bookings (detailed response)", bookingIds.size());
        
        try {
            List<BusBookingResponse> results = busBookingService.cancelMultipleBookings(bookingIds);
            
            long successCount = results.stream()
                    .filter(r -> r.getNotes() == null || !r.getNotes().contains("❌ Error"))
                    .count();
            
            String message = String.format("Hoàn thành hủy vé: %d thành công / %d tổng", 
                    successCount, bookingIds.size());
            
            return ResponseFactory.success(results, message);
        } catch (Exception e) {
            log.error("Error in detailed bulk cancel: {}", e.getMessage());
            return ResponseFactory.error(null, "Lỗi hủy vé: " + e.getMessage(), null);
        }
    }

    /**
     * ✅ Get cancellation info for a booking
     */
    @GetMapping("/{bookingId}/cancellation-info")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCancellationInfo(
            @PathVariable Integer bookingId) {
        
        log.info("Getting cancellation info for bookingId: {}", bookingId);
        
        try {
            Map<String, Object> info = busBookingService.getCancellationInfo(bookingId);
            return ResponseFactory.success(info, "Lấy thông tin hủy vé thành công.");
        } catch (Exception e) {
            log.error("Error getting cancellation info: {}", e.getMessage());
            return ResponseFactory.error(null, "Lỗi lấy thông tin: " + e.getMessage(), null);
        }
    }

    /**
     * ✅ Check if booking can be cancelled
     */
    @GetMapping("/{bookingId}/can-cancel")
    public ResponseEntity<ApiResponse<Boolean>> canBeCancelled(@PathVariable Integer bookingId) {
        log.info("Checking if bookingId {} can be cancelled", bookingId);
        
        try {
            boolean canCancel = busBookingService.canBeCancelled(bookingId);
            String message = canCancel ? "Có thể hủy vé" : "Không thể hủy vé";
            return ResponseFactory.success(canCancel, message);
        } catch (Exception e) {
            log.error("Error checking cancellation: {}", e.getMessage());
            return ResponseFactory.error(null, "Lỗi kiểm tra: " + e.getMessage(), null);
        }
    }

    /**
     * ✅ Get pending refund requests
     */
    @GetMapping("/refund-requests")
    public ResponseEntity<ApiResponse<List<BusBookingResponse>>> getPendingRefundRequests() {
        log.info("Getting pending refund requests");
        
        try {
            List<BusBookingResponse> requests = busBookingService.getPendingRefundRequests();
            return ResponseFactory.success(requests, 
                "Lấy danh sách yêu cầu hoàn tiền thành công (" + requests.size() + " yêu cầu).");
        } catch (Exception e) {
            log.error("Error getting refund requests: {}", e.getMessage());
            return ResponseFactory.error(null, "Lỗi lấy danh sách: " + e.getMessage(), null);
        }
    }

    /**
     * ✅ Process refund request
     */
    @PostMapping("/{bookingId}/process-refund")
    public ResponseEntity<ApiResponse<BusBookingResponse>> processRefund(
            @PathVariable Integer bookingId,
            @RequestBody Map<String, Object> request) {
        
        Boolean approve = (Boolean) request.get("approve");
        log.info("Processing refund for bookingId {} - approve: {}", bookingId, approve);
        
        try {
            BusBookingResponse response = busBookingService.processRefund(bookingId, approve);
            String message = approve ? "Đã phê duyệt hoàn tiền" : "Đã từ chối hoàn tiền";
            return ResponseFactory.success(response, message);
        } catch (Exception e) {
            log.error("Error processing refund: {}", e.getMessage());
            return ResponseFactory.error(null, "Lỗi xử lý hoàn tiền: " + e.getMessage(), null);
        }
    }
} 