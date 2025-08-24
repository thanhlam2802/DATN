package backend.backend.controller;

import backend.backend.dto.BookingTourDto;
import backend.backend.dto.BookingTourRequestDto;
import backend.backend.dto.MyTourBookingDTO;
import backend.backend.entity.ApiResponse;
import backend.backend.service.BookingTourService;
import backend.backend.utils.ResponseFactory;

import java.util.List;
import java.util.Map;

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
    
 // --- METHOD MỚI ĐƯỢC THÊM VÀO ---
    /**
     * Endpoint để lấy tất cả các booking của người dùng đang đăng nhập.
     *
     * @return Một danh sách các booking của người dùng.
     */
    @GetMapping("/my-bookings")
    public ResponseEntity<ApiResponse<List<MyTourBookingDTO>>> getMyBookings() {
        // Gọi phương thức service đã có để lấy dữ liệu
        List<MyTourBookingDTO> myBookings = bookingTourService.getMyTourBookings();
        // Trả về kết quả thành công bằng ResponseFactory
        return ResponseFactory.success(myBookings, "Lấy danh sách booking của bạn thành công.");
    }
    
 // --- HÀM MỚI DÀNH CHO ADMIN ĐƯỢC THÊM VÀO ĐÂY ---
    /**
     * [ADMIN] Endpoint để lấy tất cả booking và lọc theo tham số.
     * URL: GET /api/v1/bookings/tours/admin/all?tourId=1&status=PAID
     *
     * @param params Các tham số lọc được truyền vào URL.
     * @return Một danh sách các booking đã được lọc.
     */
    @GetMapping("/admin/all")
    public ResponseEntity<ApiResponse<List<MyTourBookingDTO>>> getAllBookingsForAdmin(
            @RequestParam Map<String, String> params) {
        
        // Gọi phương thức service để xử lý việc lọc
        List<MyTourBookingDTO> bookings = bookingTourService.filterAdminBookings(params);
        return ResponseFactory.success(bookings, "Lấy danh sách booking của admin thành công.");
    }
    /**
     * Endpoint để cập nhật thông tin của một booking tour bằng ID.
     * URL: PUT /api/v1/bookings/tours/{id}
     *
     * @param id ID của booking tour cần cập nhật.
     * @param requestDto Dữ liệu cập nhật, được gửi trong body của request.
     * @return Thông tin chi tiết của booking sau khi cập nhật.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BookingTourDto>> updateBookingTour(
            @PathVariable Integer id,
            @RequestBody BookingTourRequestDto requestDto) {
        
        BookingTourDto updatedBooking = bookingTourService.updateBookingTour(id, requestDto);
        return ResponseFactory.success(updatedBooking, "Cập nhật booking tour thành công!");
    }
}