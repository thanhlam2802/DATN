package backend.backend.controller;

import backend.backend.dto.ApplyVoucherRequest;
import backend.backend.dto.OrderDto;
import backend.backend.entity.ApiResponse;
import backend.backend.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

/**
 * Controller xử lý các tác vụ liên quan đến voucher qua WebSocket.
 */
@Controller
@RequiredArgsConstructor
public class VoucherSocketController {

    private final OrderService orderService;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Lắng nghe tin nhắn áp dụng voucher từ client qua WebSocket.
     * Đích đến: /app/orders/{orderId}/apply-voucher
     *
     * @param orderId ID của đơn hàng.
     * @param request Payload chứa mã voucher.
     */
    @MessageMapping("/orders/{orderId}/apply-voucher")
    public void applyVoucher(@DestinationVariable Integer orderId, @Payload ApplyVoucherRequest request) {
        // Kênh riêng để gửi phản hồi về cho client đang xem đơn hàng này
        String destination = "/topic/orders/" + orderId;

        try {
            // Tái sử dụng logic nghiệp vụ đã có trong OrderService
            OrderDto updatedOrder = orderService.applyVoucherToOrder(orderId, request.getVoucherCode());

            // Nếu thành công, tạo và gửi một response thành công
            // SỬA LẠI: Thêm <OrderDto> để khai báo tường minh
            ApiResponse<OrderDto> response = new ApiResponse<OrderDto>(
                HttpStatus.OK.value(), // 200
                "Áp dụng voucher thành công!",
                null, // không có mã lỗi
                updatedOrder
            );
            messagingTemplate.convertAndSend(destination, response);

        } catch (Exception e) {
           
            ApiResponse<Object> errorResponse = new ApiResponse<Object>(
                HttpStatus.BAD_REQUEST.value(),
                e.getMessage(),
                "VOUCHER_APPLICATION_FAILED",
                null
            );
            messagingTemplate.convertAndSend(destination, errorResponse);
        }
    }
}
