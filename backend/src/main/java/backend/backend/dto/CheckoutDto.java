package backend.backend.dto;

import java.util.Map;

import lombok.Data;

/**
 * DTO chứa thông tin cần thiết để checkout một giỏ hàng.
 */
@Data
public class CheckoutDto {

	/**
     * ID của giỏ hàng (TicketDetail) cần được xử lý.
     */
    private Integer ticketDetailId;

    /**
     * ID của voucher mà người dùng muốn áp dụng.
     * Có thể là null nếu không áp dụng voucher.
     */
    private Integer voucherId; 

    /**
     * Phương thức thanh toán được chọn (ví dụ: "CREDIT_CARD", "MOMO", "BANK_TRANSFER").
     */
    private String paymentMethod;

    /**
     * Một Map chứa các chi tiết cụ thể cho phương thức thanh toán.
     * Ví dụ: 
     * - Với thẻ tín dụng: {"cardNumber": "...", "cvc": "...", "expiryDate": "..."}
     * - Với ví điện tử: {"transactionToken": "..."}
     */
    private Map<String, Object> paymentDetails;
}