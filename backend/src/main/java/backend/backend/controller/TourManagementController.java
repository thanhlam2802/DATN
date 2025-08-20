package backend.backend.controller;

import backend.backend.dto.*;
import backend.backend.entity.ApiResponse;
import backend.backend.entity.TourStatus;
import backend.backend.service.TourManagementService;
import backend.backend.utils.ResponseFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/manage")
public class TourManagementController {

    @Autowired
    private TourManagementService tourManagementService;

    /**
     * API cho Tab 1: Lấy danh sách tóm tắt các nhà cung cấp và số tour.
     * GET /api/manage/vendors/summary
     */
    @GetMapping("/vendors/summary")
    public ResponseEntity<ApiResponse<List<VendorSummaryDTO>>> getVendorSummary() {
        List<VendorSummaryDTO> summary = tourManagementService.getVendorSummary();
        return ResponseFactory.success(summary, "Lấy danh sách nhà cung cấp thành công.");
    }
    @GetMapping("/vendors/{vendorId}/tours")
    public ResponseEntity<ApiResponse<List<TourListAdminDTO>>> getToursByVendor(@PathVariable Long vendorId) {
        List<TourListAdminDTO> tours = tourManagementService.findToursByCriteria(null, null, vendorId, null);
        return ResponseFactory.success(tours, "Lấy danh sách tour thành công.");
    }

    /**
     * API cho Tab 2: Lấy danh sách tour có bộ lọc.
     * GET /api/manage/tours?search=...&status=...&vendorId=...&destination=...
     */
    @GetMapping("/tours")
    public ResponseEntity<ApiResponse<List<TourListAdminDTO>>> getFilteredTours(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) TourStatus status,
            @RequestParam(required = false) Long vendorId,
            @RequestParam(required = false) String destination) {

        List<TourListAdminDTO> tours = tourManagementService.findToursByCriteria(search, status, vendorId, destination);
        return ResponseFactory.success(tours, "Lấy danh sách tour thành công.");
    }

    /**
     * API cho Tab 2: Cập nhật trạng thái tour.
     * PATCH /api/manage/tours/{id}/status
     */
    @PatchMapping("/tours/{id}/status")
    public ResponseEntity<ApiResponse<TourListAdminDTO>> updateTourStatus(
            @PathVariable Long id,
            @RequestBody UpdateTourStatusDTO statusDTO) {

        TourListAdminDTO updatedTour = tourManagementService.updateTourStatus(id, statusDTO.getStatus());
        return ResponseFactory.success(updatedTour, "Cập nhật trạng thái tour thành công.");
    }

    
}
