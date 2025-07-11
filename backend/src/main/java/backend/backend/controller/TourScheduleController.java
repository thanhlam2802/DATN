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

    

  
    @GetMapping("/tour/{tourId}")
    public ResponseEntity<ApiResponse<List<TourScheduleDto>>> getSchedulesByTourId(@PathVariable Long tourId) {
        List<TourScheduleDto> schedules = tourScheduleService.getSchedulesByTourId(tourId);
        return ResponseFactory.success(schedules, "Lấy danh sách lịch trình theo tour ID thành công");
    }

 
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TourScheduleDto>> getTourScheduleById(@PathVariable Long id) {
       
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED, "Get by ID not yet implemented in service");
    }

    // --- CREATE a new Tour Schedule ---
    @PostMapping
    public ResponseEntity<ApiResponse<TourScheduleDto>> createTourSchedule(@Valid @RequestBody TourScheduleDto tourScheduleDto) {
    	
    	
    	TourScheduleDto createdSchedule = tourScheduleService.createTourSchedule(tourScheduleDto);
       
        return ResponseFactory.created(createdSchedule, "Thêm lịch trình tour thành công");
    }

    // --- UPDATE an existing Tour Schedule with Activities ---
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TourScheduleDto>> updateTourSchedule(@PathVariable Long id, @Valid @RequestBody TourScheduleDto tourScheduleDto) {
    	 System.out.print("id:" + id);
         System.out.print(tourScheduleDto);
    	TourScheduleDto updatedSchedule = tourScheduleService.updateTourSchedule(id, tourScheduleDto);
        return ResponseFactory.success(updatedSchedule, "Cập nhật lịch trình tour thành công");
    }

    // --- DELETE a Tour Schedule and its Activities ---
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTourSchedule(@PathVariable Long id) {
        tourScheduleService.deleteTourSchedule(id);
        return ResponseFactory.success(null, "Xóa lịch trình tour thành công");
    }
}