package backend.backend.controller;

import backend.backend.dto.TourScheduleDto;
import backend.backend.entity.ApiResponse;
import backend.backend.service.TourScheduleService;
import backend.backend.utils.ResponseFactory;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus; 
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException; 

import java.util.List;

@RestController
@RequestMapping("/api/v1/tour-schedules") 
@CrossOrigin("*") 
public class TourScheduleController {
@Autowired
TourScheduleService tourScheduleService;

    

    // --- GET all schedules for a specific tour (if not handled by TourController already) ---
    // Note: If TourController's /{id}/schedule is already fetching by tourId,
    // this might be redundant or for a different purpose (e.g., getting ALL schedules, not just for a specific tour)
    // I'll keep the tourId based fetching here for completeness.
    @GetMapping("/tour/{tourId}")
    public ResponseEntity<ApiResponse<List<TourScheduleDto>>> getSchedulesByTourId(@PathVariable Long tourId) {
        List<TourScheduleDto> schedules = tourScheduleService.getSchedulesByTourId(tourId);
        return ResponseFactory.success(schedules, "Lấy danh sách lịch trình theo tour ID thành công");
    }

    // --- GET a specific schedule by its own ID ---
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TourScheduleDto>> getTourScheduleById(@PathVariable Long id) {
        // Assuming TourScheduleService has a method to get single schedule by ID (we need to add this)
        // For now, let's throw a NOT_FOUND if it's not implemented or found.
        // You'll need to add: Optional<TourScheduleDto> getTourScheduleById(Long id); to TourScheduleService
        // and implement it in TourScheduleServiceImpl (using tourScheduleDAO.findById() and mapping to DTO)
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED, "Get by ID not yet implemented in service");
    }

    // --- CREATE a new Tour Schedule ---
    @PostMapping
    public ResponseEntity<ApiResponse<TourScheduleDto>> createTourSchedule(@Valid @RequestBody TourScheduleDto tourScheduleDto) {
        TourScheduleDto createdSchedule = tourScheduleService.createTourSchedule(tourScheduleDto);
        return ResponseFactory.success(createdSchedule, "Thêm lịch trình tour thành công", HttpStatus.CREATED); // Use HttpStatus.CREATED for POST
    }

    // --- UPDATE an existing Tour Schedule ---
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TourScheduleDto>> updateTourSchedule(@PathVariable Long id, @Valid @RequestBody TourScheduleDto tourScheduleDto) {
        TourScheduleDto updatedSchedule = tourScheduleService.updateTourSchedule(id, tourScheduleDto);
        return ResponseFactory.success(updatedSchedule, "Cập nhật lịch trình tour thành công");
    }

    // --- DELETE a Tour Schedule ---
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTourSchedule(@PathVariable Long id) {
        tourScheduleService.deleteTourSchedule(id);
        return ResponseFactory.success(null, "Xóa lịch trình tour thành công"); // No data returned for delete
    }
}