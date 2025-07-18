package backend.backend.dto;

import lombok.Data;

/**
 * DTO để nhận yêu cầu "Mua ngay" (thanh toán trực tiếp) cho một tour cụ thể,
 * bỏ qua giỏ hàng.
 */
@Data
public class DirectTourReservationRequestDto {
    
    // Thông tin để xác thực người dùng
    private Integer userId;

    // Thông tin về tour và ngày khởi hành đã chọn
    private Long tourId;
    private Long departureId;

    // Thông tin về số lượng khách
    private int numberOfAdults;
    private int numberOfChildren;

    // Thông tin liên hệ của khách hàng
    private String customerName;
    private String phone;
    private String email;
    private String notes;
}
