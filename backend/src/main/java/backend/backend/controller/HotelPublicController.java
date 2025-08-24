package backend.backend.controller;

import backend.backend.dto.*;
import backend.backend.dto.Hotel.HotelDetailDto;
import backend.backend.dto.Hotel.HotelDto;
import backend.backend.dto.Hotel.HotelSearchRequestDto;
import backend.backend.dto.Hotel.HotelBookingRequestDto;
import backend.backend.dto.Hotel.UpdateHotelBookingRequestDto;
import backend.backend.dto.OrderDto;
import backend.backend.entity.ApiResponse;
import backend.backend.entity.User;
import backend.backend.service.HotelService;
import backend.backend.service.HotelBookingService;
import backend.backend.utils.ResponseFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestBody;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;

class CreateReviewRequest {
    public Integer rating;
    public String content;
}

@RestController
@RequestMapping("/api/v1/hotels")
@CrossOrigin(origins = {
        "https://poly-java-6-fb151.web.app",
        "https://www.travela.io.vn",
        "http://localhost:5173"
})
public class HotelPublicController {
    @Autowired
    private HotelService hotelService;
    @Autowired
    private HotelBookingService hotelBookingService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageDto<HotelDto>>> searchHotels(
            @Valid @ModelAttribute HotelSearchRequestDto requestDto) {
        PageDto<HotelDto> hotelPage = hotelService.searchHotels(requestDto);
        return ResponseFactory.success(hotelPage, "Lấy danh sách khách sạn thành công");
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<HotelDetailDto>> getHotelDetails(
            @PathVariable Integer id,
            @Valid @ModelAttribute HotelSearchRequestDto requestDto) {
        HotelDetailDto hotelDetail = hotelService.getHotelDetails(id, requestDto);
        return ResponseFactory.success(hotelDetail, "Lấy chi tiết khách sạn thành công");
    }

    @GetMapping("/{id}/reviews")
    public ResponseEntity<ApiResponse<List<ReviewDto>>> getHotelReviews(@PathVariable Integer id) {
        List<ReviewDto> reviews = hotelService.getReviewsForHotel(id);
        return ResponseFactory.success(reviews, "Lấy danh sách đánh giá thành công");
    }

    @PostMapping("/{id}/reviews")
    public ResponseEntity<ApiResponse<String>> createHotelReview(
            @PathVariable Integer id,
            @RequestBody CreateReviewRequest req,
            Authentication authentication) {
        if (authentication == null) {
            return ResponseFactory.error(HttpStatus.UNAUTHORIZED, "Bạn cần đăng nhập để gửi đánh giá!", null);
        }
        String email;
        if (authentication.getPrincipal() instanceof User) {
            User user = (User) authentication.getPrincipal();
            email = user.getEmail();
        } else {
            email = authentication.getName();
        }
        
        hotelService.createHotelReview(id, email, req.rating, req.content);
        return ResponseFactory.success(null, "Đánh giá đã được gửi thành công");
    }

    @PostMapping("/book")
    public ResponseEntity<ApiResponse<OrderDto>> bookHotel(@RequestBody HotelBookingRequestDto dto, Authentication authentication) {
        if (authentication == null) {
            return ResponseFactory.error(HttpStatus.UNAUTHORIZED, "Bạn cần đăng nhập để đặt phòng!", null);
        }
        OrderDto order = hotelBookingService.bookHotel(dto, authentication);
        return ResponseFactory.success(order, "Đặt phòng thành công. Vui lòng thanh toán để xác nhận.");
    }

    @PutMapping("/bookings/update")
    public ResponseEntity<ApiResponse<OrderDto>> updateHotelBooking(@RequestBody UpdateHotelBookingRequestDto dto, Authentication authentication) {
        if (authentication == null) {
            return ResponseFactory.error(HttpStatus.UNAUTHORIZED, "Bạn cần đăng nhập để cập nhật booking!", null);
        }
        OrderDto order = hotelBookingService.updateHotelBooking(dto, authentication);
        return ResponseFactory.success(order, "Cập nhật booking thành công.");
    }

    @PostMapping("/bookings/{bookingId}/cancel")
    public ResponseEntity<ApiResponse<String>> cancelHotelBooking(
            @PathVariable Integer bookingId, 
            Authentication authentication) {
        if (authentication == null) {
            return ResponseFactory.error(HttpStatus.UNAUTHORIZED, "Bạn cần đăng nhập để hủy booking!", null);
        }
        
        try {
            hotelBookingService.cancelHotelBooking(bookingId);
            return ResponseFactory.success("OK", "Hủy đặt phòng khách sạn thành công. Phòng đã được hoàn trả.");
        } catch (Exception e) {
            return ResponseFactory.error(HttpStatus.BAD_REQUEST, "Không thể hủy booking: " + e.getMessage(), null);
        }
    }

    @GetMapping("/popular-by-bookings")
    public ResponseEntity<ApiResponse<List<HotelDto>>> getPopularHotelsByBookings(
            @RequestParam(defaultValue = "10") int size) {
        List<HotelDto> popularHotels = hotelService.getPopularHotelsByBookings(size);
        return ResponseFactory.success(popularHotels, "Lấy danh sách khách sạn phổ biến theo đơn hàng thành công");
    }
}