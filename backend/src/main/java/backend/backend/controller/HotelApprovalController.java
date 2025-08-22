package backend.backend.controller;

import backend.backend.dto.Hotel.HotelApprovalRequestDto;
import backend.backend.service.HotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/hotels")
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class HotelApprovalController {

    @Autowired
    private HotelService hotelService;

    @PutMapping("/{id}/approve")
    public ResponseEntity<?> approveHotel(@PathVariable Integer id) {
        try {
            hotelService.approveHotel(id);
            return ResponseEntity.ok(Map.of("message", "Khách sạn đã được duyệt thành công"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<?> rejectHotel(@PathVariable Integer id, @RequestBody HotelApprovalRequestDto request) {
        try {
            if (request.getReason() == null || request.getReason().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Lý do từ chối không được để trống"));
            }
            
            hotelService.rejectHotel(id, request.getReason());
            return ResponseEntity.ok(Map.of("message", "Khách sạn đã bị từ chối"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}/resubmit")
    public ResponseEntity<?> resubmitHotel(@PathVariable Integer id) {
        try {
            hotelService.resubmitHotel(id);
            return ResponseEntity.ok(Map.of("message", "Khách sạn đã được gửi lại để duyệt"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
