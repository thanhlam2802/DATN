package backend.backend.dto.BusDTO;

import lombok.Data;

import java.util.List;

@Data
public class DirectBusReservationRequestDto {
    /**
     * ID của bus slot muốn đặt
     */
    private Integer busSlotId;

    /**
     * Danh sách ghế đã chọn
     */
    private List<String> selectedSeatNumbers;

    /**
     * Thông tin khách hàng
     */
    private String customerName;
    private String phone;
    private String email;
    private String notes;

    /**
     * ID người dùng (tạm thời, sau này lấy từ JWT)
     */
    private Integer userId;
}
