package backend.backend.payload.request;



import lombok.Data;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO chứa dữ liệu cần thiết để tạo một booking tour mới.
 * Dữ liệu này được gửi từ client lên server qua API.
 */
@Data
public class CreateTourBookingRequest {

    /**
     * ID của ngày khởi hành (Departure) mà người dùng đã chọn.
     * Bắt buộc phải có để lấy thông tin giá và tour.
     */
    @NotNull(message = "ID ngày khởi hành không được để trống")
    private Long departureId;

    /**
     * Tên của khách hàng đặt tour.
     */
    @NotBlank(message = "Tên khách hàng không được để trống")
    private String customerName;

    /**
     * Số điện thoại liên lạc của khách hàng.
     */
    @NotBlank(message = "Số điện thoại không được để trống")
    private String phone;

    /**
     * Số lượng người lớn tham gia tour.
     * Tối thiểu là 1 nếu không có trẻ em.
     */
    @NotNull(message = "Số lượng người lớn không được để trống")
    @Min(value = 0, message = "Số lượng người lớn không được âm")
    private Integer numberOfAdults;

    /**
     * Số lượng trẻ em tham gia tour.
     */
    @NotNull(message = "Số lượng trẻ em không được để trống")
    @Min(value = 0, message = "Số lượng trẻ em không được âm")
    private Integer numberOfChildren;

    /**
     * Ghi chú thêm của khách hàng cho tour.
     * Có thể để trống.
     */
    private String notes;
    
    /**
     * ID của giỏ hàng (TicketDetail) mà booking này sẽ được thêm vào.
     * Cần thiết để liên kết booking với một phiên giao dịch cụ thể.
     */
    @NotNull(message = "ID của giỏ hàng không được để trống")
    private Integer ticketDetailId;

}