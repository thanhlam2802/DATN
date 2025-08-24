package backend.backend.entity.enumBus;

public enum BusBookingStatus {
    RESERVED,           // Trong giỏ hàng, tạm giữ ghế
    CONFIRMED,          // Đã checkout, chờ thanh toán
    PAID,               // Đã thanh toán thành công
    CANCELLED,          // Đã hủy
    EXPIRED,            // Hết hạn
    REFUND_REQUESTED,   // Yêu cầu hoàn tiền
    REFUND_PROCESSING,  // Đang xử lý hoàn tiền
    REFUNDED,           // Đã hoàn tiền
    REFUND_REJECTED     // Từ chối hoàn tiền
}
