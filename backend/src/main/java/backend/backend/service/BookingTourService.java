package backend.backend.service;

import java.util.List;
import java.util.Map;

import backend.backend.dto.BookingTourDto;
import backend.backend.dto.BookingTourRequestDto;
import backend.backend.dto.MyTourBookingDTO;

/**
 * Interface định nghĩa các dịch vụ liên quan đến việc đặt tour.
 */
public interface BookingTourService {

    /**
     * Tạo một booking tour mới dựa trên yêu cầu từ người dùng và thêm vào giỏ hàng.
     *
     * @param requestDto Đối tượng chứa thông tin cần thiết để đặt tour (userId, tourId, orderId, ...).
     * @return Một đối tượng DTO chứa thông tin chi tiết của booking vừa được tạo.
     */
    BookingTourDto createBookingTour(BookingTourRequestDto requestDto);

    /**
     * Lấy thông tin chi tiết của một lần đặt tour (một sản phẩm trong giỏ hàng) bằng ID của nó.
     *
     * @param id ID của BookingTour.
     * @return Một đối tượng DTO chứa thông tin chi tiết của booking.
     */
    BookingTourDto getBookingTourById(Integer id);
    
    
    List<MyTourBookingDTO> getMyTourBookings();

	List<MyTourBookingDTO> filterAdminBookings(Map<String, String> params);

}