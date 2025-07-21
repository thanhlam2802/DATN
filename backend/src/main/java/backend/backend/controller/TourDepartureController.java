package backend.backend.controller;

import backend.backend.dto.DepartureDto;
import backend.backend.entity.ApiResponse;
import backend.backend.payload.request.RecurringDepartureRequest; 
import backend.backend.service.DepartureService;
import backend.backend.utils.ResponseFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")

public class TourDepartureController {
    @Autowired
    DepartureService departureService;

    /**
     * API để lấy danh sách ngày khởi hành theo ID của tour.
     */
    @GetMapping("/tours/{tourId}/departures")
    public ResponseEntity<ApiResponse<List<DepartureDto>>> getDeparturesByTour(@PathVariable Long tourId) {
        List<DepartureDto> departureDtos = departureService.getDeparturesByTourId(tourId);
        return ResponseFactory.success(departureDtos, "Lấy danh sách ngày khởi hành thành công.");
    }

    /**
     * API để tạo một ngày khởi hành mới cho một tour.
     * POST /api/tours/{tourId}/departures
     */
    @PostMapping("/tours/{tourId}/departures")
    public ResponseEntity<ApiResponse<DepartureDto>> createDeparture(
            @PathVariable Long tourId,
            @RequestBody DepartureDto departureDto) {
        DepartureDto createdDeparture = departureService.createDeparture(tourId, departureDto);
        return ResponseFactory.created(createdDeparture, "Thêm ngày khởi hành thành công.");
    }

    /**
     * MỚI: API để tạo hàng loạt ngày khởi hành theo chu kỳ.
     * POST /api/tours/{tourId}/departures/recurring
     */
    @PostMapping("/tours/{tourId}/departures/recurring")
    public ResponseEntity<ApiResponse<List<DepartureDto>>> createRecurringDepartures(
            @PathVariable Long tourId,
            @RequestBody RecurringDepartureRequest request) {

        // Gọi phương thức service đã tạo
        List<DepartureDto> createdDepartures = departureService.createRecurringDepartures(
                tourId,
                request.getTemplateDto(),
                request.getIntervalDays(),
                request.getCount());

        // Sử dụng ResponseFactory để trả về response nhất quán
        return ResponseFactory.created(createdDepartures, "Tạo hàng loạt ngày khởi hành thành công.");
    }

    /**
     * API để cập nhật thông tin một ngày khởi hành đã có.
     * PUT /api/departures/{departureId}
     */
    @PutMapping("/departures/{departureId}")
    public ResponseEntity<ApiResponse<DepartureDto>> updateDeparture(
            @PathVariable Long departureId,
            @RequestBody DepartureDto departureDto) {
        DepartureDto updatedDeparture = departureService.updateDeparture(departureId, departureDto);
        return ResponseFactory.success(updatedDeparture, "Cập nhật ngày khởi hành thành công.");
    }

    /**
     * API để xóa một ngày khởi hành.
     * DELETE /api/departures/{departureId}
     */
    @DeleteMapping("/departures/{departureId}")
    public ResponseEntity<ApiResponse<Object>> deleteDeparture(@PathVariable Long departureId) {
        departureService.deleteDeparture(departureId);
        return ResponseFactory.success(null, "Xóa ngày khởi hành thành công.");
    }
}


