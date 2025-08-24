package backend.backend.controller;

import backend.backend.dao.BusBookingDAO;
import backend.backend.dao.UserDAO;
import backend.backend.dto.ApplyVoucherRequest;
import backend.backend.dto.CheckoutDto;
import backend.backend.dto.DirectTourReservationRequestDto;
import backend.backend.dto.OrderDto; 
import backend.backend.entity.ApiResponse;
import backend.backend.exception.ResourceNotFoundException;
import backend.backend.service.OrderService;
import backend.backend.service.busService.BusBookingService;
import backend.backend.utils.ResponseFactory;
import jakarta.validation.Valid;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import backend.backend.dto.DirectFlightReservationRequestDto;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Log4j2
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);


    private final OrderService orderService;
    private final BusBookingDAO busBookingDAO;
    private final BusBookingService busBookingService;

    private final UserDAO userDAO; // Thêm DAO để kiểm tra người dùng

    /**
     * MỚI: Endpoint để lấy tất cả đơn hàng của một người dùng.
     * Sẽ xử lý cho URL: GET /api/v1/orders/my-orders?userId=1
     */
    @GetMapping("/my-orders")
    public ResponseEntity<ApiResponse<List<OrderDto>>> getMyOrders(@RequestParam Integer userId) {
        // Thêm bước kiểm tra để đảm bảo người dùng tồn tại
        userDAO.findById(userId)
             .orElseThrow(() -> new ResourceNotFoundException("Người dùng với ID " + userId + " không tồn tại."));
        
        List<OrderDto> userOrders = orderService.getOrdersByUserId(userId);
        return ResponseFactory.success(userOrders, "Lấy danh sách đơn hàng thành công.");
    }

    /**
     * MỚI: Endpoint xử lý "Mua ngay" cho một tour cụ thể.
     * Tạo một đơn hàng tạm thời và giữ chỗ trong 30 phút.
     * @param directRequest Dữ liệu đặt tour trực tiếp từ người dùng.
     * @return Thông tin của đơn hàng tạm vừa được tạo.
     */
    @PostMapping("/reserve-tour-direct")
    public ResponseEntity<ApiResponse<OrderDto>> createDirectTourReservation(@RequestBody DirectTourReservationRequestDto directRequest) {
        // Bạn cần tạo phương thức này trong OrderService
        OrderDto temporaryOrder = orderService.createDirectTourReservation(directRequest);
        return ResponseFactory.created(temporaryOrder, "Giữ chỗ thành công. Vui lòng hoàn tất thanh toán trong 30 phút.");
    }
    /**
     * Endpoint để xử lý thanh toán giỏ hàng và tạo một hóa đơn duy nhất.
     */
    @PostMapping("/checkout")
    public ResponseEntity<ApiResponse<OrderDto>> checkout(@RequestBody CheckoutDto checkoutDto) {
        // Gọi service, giờ đây trả về OrderDto
        OrderDto createdOrder = orderService.placeOrder(checkoutDto);
        
        // Trả về hóa đơn vừa được tạo
        return ResponseFactory.created(createdOrder, "Thanh toán và tạo hóa đơn thành công!");
    }

    /**
     * Endpoint để lấy thông tin chi tiết của một hóa đơn bằng ID của nó.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderDto>> getOrderById(@PathVariable Integer id) {
        // Giả sử OrderService có phương thức getOrderById trả về OrderDto
        OrderDto orderDetails = orderService.getOrderById(id);
        
        return ResponseFactory.success(orderDetails, "Lấy thông tin hóa đơn thành công.");
    }

    /**
     * Endpoint xử lý giữ chỗ (mua ngay) cho flight slot.
     */
    @PostMapping("/reserve-flight-direct")
    public ResponseEntity<ApiResponse<Integer>> createDirectFlightReservation(@RequestBody DirectFlightReservationRequestDto directRequest) {
        Integer idBooing = orderService.createDirectFlightReservation(directRequest);
        return ResponseFactory.created(idBooing, "Giữ chỗ thành công. Vui lòng hoàn tất thanh toán trong thời gian quy định.");
    }
    @PutMapping("/success-order/{id}")
    public ResponseEntity<ApiResponse<OrderDto>> successOrder(@PathVariable String id, @RequestBody String transactionId) {
        logger.info("Bắt đầu xử lý thanh toán thành công cho Order ID: {} với Transaction ID: {}", id, transactionId);
        try {
            OrderDto temporaryOrder = orderService.paidOrder(Integer.valueOf(id),transactionId);
            logger.info("Thanh toán thành công cho Order ID: {} được ghi nhận.", id);
            return ResponseFactory.created(temporaryOrder, "Thanh toán thành công.");
        } catch (Exception e) {
            logger.error("Lỗi khi xử lý thanh toán cho Order ID: {}. Chi tiết lỗi: {}", id, e.getMessage());
          
            throw e; 
        }
        
       
        
    }
    
 
    /**
     * API để áp dụng một mã giảm giá vào đơn hàng đang chờ thanh toán.
     * @param id ID của đơn hàng cần áp dụng.
     * @param request Chứa voucherCode người dùng nhập.
     * @return OrderDto đã được cập nhật với giá mới.
     */
    @PostMapping("/{id}/apply-voucher")
    public ResponseEntity<ApiResponse<OrderDto>> applyVoucher(
            @PathVariable Integer id,
            @Valid @RequestBody ApplyVoucherRequest request) {
        
        // Giả sử OrderService có phương thức applyVoucherToOrder
        OrderDto updatedOrder = orderService.applyVoucherToOrder(id, request.getVoucherCode());
        return ResponseFactory.success(updatedOrder, "Áp dụng mã giảm giá thành công.");
    }

    @PutMapping("/{orderId}/cancel-after-refund")
    public ResponseEntity<ApiResponse<String>> cancelOrderAfterRefund(@PathVariable Integer orderId) {
        logger.info("Bắt đầu xử lý hủy đơn hàng sau khi hoàn tiền cho Order ID: {}", orderId);
        
        try {
            // Gọi service để xử lý logic hủy đơn hàng và các booking liên quan
            orderService.cancelOrderAfterRefund(orderId);
            
            logger.info("Đã hủy đơn hàng {} thành công sau khi hoàn tiền", orderId);
            return ResponseFactory.success("OK", "Đã hủy đơn hàng thành công sau khi hoàn tiền. Trạng thái đã được cập nhật thành REFUNDED.");
            
        } catch (Exception e) {
            logger.error("Lỗi khi hủy đơn hàng {} sau khi hoàn tiền: {}", orderId, e.getMessage());
            return ResponseFactory.error(HttpStatus.INTERNAL_SERVER_ERROR, 
                "Không thể hủy đơn hàng sau khi hoàn tiền: " + e.getMessage(), null);
        }
    }
}