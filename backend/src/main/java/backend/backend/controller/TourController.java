package backend.backend.controller;


import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import backend.backend.dao.TourScheduleDAO;
import backend.backend.dto.DepartureDto;
import backend.backend.dto.ItineraryDayDto;
import backend.backend.dto.PageDto;
import backend.backend.dto.ReviewDto;
import backend.backend.dto.TourDetailDto;
import backend.backend.dto.TourDto;
import backend.backend.dto.TourSearchRequestDto;
import backend.backend.entity.ApiResponse; // Import lớp ApiResponse
import backend.backend.entity.TourSchedule;
import backend.backend.service.TourService;
import backend.backend.utils.ResponseFactory; // Import ResponseFactory
import io.jsonwebtoken.io.IOException;
import jakarta.persistence.criteria.Path;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/tours")
@CrossOrigin("*")
public class TourController {

    @Autowired
     TourService tourService;
    @Autowired
    TourScheduleDAO tourScheduleService;


    @GetMapping
    public ResponseEntity<ApiResponse<PageDto<TourDto>>> searchTours(@Valid @ModelAttribute TourSearchRequestDto requestDto) {
        PageDto<TourDto> tourPage = tourService.searchTours(requestDto);
        return ResponseFactory.success(tourPage, "Lấy danh sách tour thành công");
    }
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TourDetailDto>> getTourDetailsById(@PathVariable Long id) {
        TourDetailDto tourDetailDto = tourService.getTourDetailsById(id);
        return ResponseFactory.success(tourDetailDto, "Lấy chi tiết tour thành công");
    }

    @GetMapping("/{id}/reviews")
    public ResponseEntity<ApiResponse<List<ReviewDto>>> getReviewsForTour(@PathVariable Integer id) { // Corrected type to Long
        List<ReviewDto> reviewDtos = tourService.getReviewsForTour(id);
        return ResponseFactory.success(reviewDtos, "Lấy danh sách đánh giá thành công");
    }

 
    @GetMapping("/{id}/departures")
    public ResponseEntity<ApiResponse<List<DepartureDto>>> getDeparturesForTour(@PathVariable Long id) {
        List<DepartureDto> departureDtos = tourService.getDeparturesForTour(id);
        return ResponseFactory.success(departureDtos, "Lấy danh sách lịch khởi hành thành công");
    }
    
    // --- NEW ENDPOINT FOR TOUR SCHEDULE ---
    @GetMapping("/{id}/schedule") 
    public ResponseEntity<ApiResponse<List<TourSchedule>>> getTourSchedule(@PathVariable Long id) {
        List<TourSchedule> tourSchedules = tourScheduleService.getSchedulesByTourId(id);
        return ResponseFactory.success(tourSchedules, "Lấy lịch trình tour thành công");
    }
    @GetMapping("/{id}/itinerary")
    public ResponseEntity<ApiResponse<List<ItineraryDayDto>>> getTourItinerary(@PathVariable Long id) {
        List<ItineraryDayDto> itinerary = tourService.getStructuredItinerary(id);
        return ResponseFactory.success(itinerary, "Lấy lịch trình chi tiết thành công");
    }
//    @PostMapping("/book-tour")
//    public ResponseEntity<ApiResponse<BookingConfirmationDto>> handleTourBooking(
//            @Valid @RequestBody BookingRequestDto bookingRequest) {
//        BookingConfirmationDto confirmation = bookingService.createBooking(bookingRequest);
//        return ResponseFactory.success(confirmation, "Đặt tour thành công!", HttpStatus.CREATED);
//    }
}