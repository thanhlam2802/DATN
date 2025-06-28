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

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/hotels")
@CrossOrigin(origins = "*")
public class HotelController {

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
}