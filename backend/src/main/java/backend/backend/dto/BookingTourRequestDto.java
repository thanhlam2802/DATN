package backend.backend.dto;

import lombok.Data;

/**
 * DTO (Data Transfer Object) chứa thông tin yêu cầu để tạo một booking tour.
 * Phiên bản này đã được cập nhật để khớp với BookingTour entity mới.
 */
@Data
public class BookingTourRequestDto {

    /**
     * ID của giỏ hàng (TicketDetail) hiện tại mà booking này sẽ được thêm vào.
     * Bắt buộc phải có để biết thêm sản phẩm vào giỏ hàng nào.
     */
    private Integer cartId;

    /**
     * ID của ngày khởi hành (Departure) mà người dùng đã chọn.
     * Thay thế cho trường departureDate cũ.
     */
    private Long  departureId;

    /**
     * Tên của khách hàng liên hệ.
     */
    private String customerName;

    /**
     * Số điện thoại của khách hàng.
     */
    private String phone;

    /**
     * Số lượng người lớn tham gia.
     * Thay thế cho trường numberOfPeople cũ.
     */
    private int numberOfAdults;

    /**
     * Số lượng trẻ em tham gia.
     * Thay thế cho trường numberOfPeople cũ.
     */
    private int numberOfChildren;

    /**
     * Ghi chú thêm của khách hàng.
     */
    private String notes;
    
    // --- Các trường này có thể vẫn cần thiết cho logic ở tầng service ---
    
    /**
     * ID của người dùng đang đăng nhập để xác thực.
     */
    private Integer userId;

    /**
     * ID của Tour để lấy thông tin về giá hoặc các thông tin khác nếu cần.
     */
    private Long tourId;

	
}