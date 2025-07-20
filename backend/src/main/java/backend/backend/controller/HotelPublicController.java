package backend.backend.controller;

import backend.backend.dto.*;
import backend.backend.dto.Hotel.HotelDetailDto;
import backend.backend.dto.Hotel.HotelDto;
import backend.backend.dto.Hotel.HotelSearchRequestDto;
import backend.backend.entity.ApiResponse;
import backend.backend.service.HotelService;
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
        String email = authentication.getName();
        hotelService.createHotelReview(id, email, req.rating, req.content);
        return ResponseFactory.success(null, "Đánh giá đã được gửi thành công");
    }
}