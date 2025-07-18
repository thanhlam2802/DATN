package backend.backend.controller;

import backend.backend.dao.UserDAO;
import backend.backend.dto.CheckoutDto;
import backend.backend.dto.DirectTourReservationRequestDto;
import backend.backend.dto.OrderDto; 
import backend.backend.entity.ApiResponse;
import backend.backend.exception.ResourceNotFoundException;
import backend.backend.service.OrderService;
import backend.backend.utils.ResponseFactory;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;
    
    @Autowired
    private UserDAO userDAO; // Thêm DAO để kiểm tra người dùng

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
}