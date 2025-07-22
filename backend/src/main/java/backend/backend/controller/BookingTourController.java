package backend.backend.controller;

import backend.backend.dto.BookingTourDto;
import backend.backend.dto.BookingTourRequestDto;
import backend.backend.entity.ApiResponse;
import backend.backend.service.BookingTourService;
import backend.backend.utils.ResponseFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/bookings/tours")
public class BookingTourController {

    @Autowired
    private BookingTourService bookingTourService;

    /**
     * Endpoint để tạo một booking tour mới (thêm tour vào giỏ hàng).
     *
     * @param requestDto Dữ liệu cần thiết để tạo booking, được gửi trong body của request.
     * @return Thông tin chi tiết của booking vừa được tạo.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<BookingTourDto>> createBooking(
            @RequestBody BookingTourRequestDto requestDto) {
        
        BookingTourDto newBooking = bookingTourService.createBookingTour(requestDto);
        return ResponseFactory.created(newBooking, "Thêm tour vào giỏ hàng thành công!");
    }

    /**
     * Endpoint để lấy thông tin chi tiết của một booking tour bằng ID của nó.
     *
     * @param id ID của booking tour, được truyền trong URL.
     * @return Thông tin chi tiết của booking được tìm thấy.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookingTourDto>> getBookingById(@PathVariable Integer id) {
        BookingTourDto bookingDto = bookingTourService.getBookingTourById(id);
        return ResponseFactory.success(bookingDto, "Lấy thông tin đặt tour thành công.");
    }
}