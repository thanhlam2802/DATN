package backend.backend.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import backend.backend.dao.TourScheduleDAO;
import backend.backend.dto.DepartureDto;
import backend.backend.dto.PageDto;
import backend.backend.dto.ReviewDto;
import backend.backend.dto.TourDetailDto;
import backend.backend.dto.TourDto;
import backend.backend.dto.TourScheduleDto;
import backend.backend.dto.TourSearchRequestDto;
import backend.backend.entity.ApiResponse; // Import lớp ApiResponse
import backend.backend.entity.TourSchedule;
import backend.backend.service.TourService;
import backend.backend.utils.ResponseFactory; // Import ResponseFactory
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
    public record BookingRequest(
            String selectedDate,
            Integer adults,
            Integer children,
            Integer infants
        ) {}

        @PostMapping("/book")
        public ResponseEntity<String> handleBooking(@RequestBody BookingRequest bookingRequest) {
            LocalDate bookingDate = LocalDate.parse(bookingRequest.selectedDate());

            System.out.println("Received booking request:");
            System.out.println("Date: " + bookingRequest.selectedDate());
            System.out.println("Adults: " + bookingRequest.adults());
            System.out.println("Children: " + bookingRequest.children());
            System.out.println("Infants: " + bookingRequest.infants());

            return ResponseEntity.ok("Booking request received successfully!");
        }
}