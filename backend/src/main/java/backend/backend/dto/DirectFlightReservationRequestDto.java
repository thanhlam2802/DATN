package backend.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO để nhận yêu cầu "Mua ngay" (thanh toán trực tiếp) cho một tour cụ thể,
 * bỏ qua giỏ hàng.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DirectFlightReservationRequestDto {

    /**
     * ID của slot ghế máy bay muốn giữ chỗ
     */
    private Integer flightSlotId;

    /**
     * Thông tin khách hàng
     */
    private String customerName;
    private String phone;
    private String email;
    private String passport;
    private String gender;
    private String dob;
    private String notes;
}
