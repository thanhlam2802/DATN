package backend.backend.controller;

import backend.backend.dto.AddItemRequestDto;
import backend.backend.dto.OrderDto;
import backend.backend.entity.ApiResponse;
import backend.backend.service.CartService;
import backend.backend.utils.ResponseFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart") 
public class CartController {

    @Autowired
    private CartService cartService;

    /**
     * Tạo một giỏ hàng mới cho người dùng.
     * @param userId ID của người dùng cần tạo giỏ hàng.
     * @return Chi tiết giỏ hàng vừa được tạo.
     */
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<OrderDto>> createCart(@RequestParam Integer userId) {
        OrderDto newCart = cartService.createCartForUser(userId);
        return ResponseFactory.created(newCart, "Tạo giỏ hàng mới thành công.");
    }

    /**
     * Lấy thông tin chi tiết của một giỏ hàng, bao gồm tất cả các mục bên trong.
     * @param orderId ID của giỏ hàng (Order).
     * @return Chi tiết giỏ hàng.
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderDto>> getCartDetails(@PathVariable Integer orderId) {
        OrderDto cart = cartService.getCartById(orderId);
        return ResponseFactory.success(cart, "Lấy thông tin giỏ hàng thành công.");
    }
    
 // --- API MỚI ĐỂ THÊM DỊCH VỤ VÀO GIỎ HÀNG ---
    /**
     * Thêm một dịch vụ (tour, khách sạn, v.v.) vào một giỏ hàng đã tồn tại.
     * Dựa vào 'itemType' trong request body để xác định và xử lý dịch vụ phù hợp.
     *
     * @param orderId ID của giỏ hàng (Order) cần thêm mục vào.
     * @param request Đối tượng chứa thông tin chi tiết về mục cần thêm.
     * @return Chi tiết giỏ hàng sau khi đã được cập nhật.
     */
    @PostMapping("/{orderId}/items")
    public ResponseEntity<ApiResponse<OrderDto>> addItemToCart(
            @PathVariable Integer orderId,
            @RequestBody AddItemRequestDto request) {
        
        // Nhiệm vụ của controller là nhận request và gọi service tương ứng
        OrderDto updatedCart = cartService.addItemToCart(orderId, request);
        return ResponseFactory.success(updatedCart, "Thêm dịch vụ vào giỏ hàng thành công.");
    }


    /**
     * Xóa một mục (booking) cụ thể khỏi giỏ hàng.
     * @param itemId ID của mục cần xóa (ví dụ: ID của FlightBooking, HotelBooking).
     * @param itemType Loại của mục cần xóa (ví dụ: "FLIGHT", "HOTEL", "BUS", "TOUR").
     * @return Chi tiết giỏ hàng sau khi đã xóa mục.
     */
    @DeleteMapping("/items")
    public ResponseEntity<ApiResponse<OrderDto>> removeItemFromCart(@RequestParam Integer itemId, @RequestParam String itemType) {
        OrderDto updatedCart = cartService.removeItemFromCart(itemId, itemType);
        return ResponseFactory.success(updatedCart, "Xóa sản phẩm khỏi giỏ hàng thành công.");
    }
}