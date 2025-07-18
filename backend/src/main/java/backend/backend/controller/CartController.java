package backend.backend.controller;

import backend.backend.dto.TicketDetailDto;
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
    public ResponseEntity<ApiResponse<TicketDetailDto>> createCart(@RequestParam Integer userId) {
        TicketDetailDto newCart = cartService.createCartForUser(userId);
        return ResponseFactory.created(newCart, "Tạo giỏ hàng mới thành công.");
    }

    /**
     * Lấy thông tin chi tiết của một giỏ hàng, bao gồm tất cả các mục bên trong.
     * @param cartId ID của giỏ hàng (TicketDetail).
     * @return Chi tiết giỏ hàng.
     */
    @GetMapping("/{cartId}")
    public ResponseEntity<ApiResponse<TicketDetailDto>> getCartDetails(@PathVariable Integer cartId) {
        TicketDetailDto cart = cartService.getCartById(cartId);
        return ResponseFactory.success(cart, "Lấy thông tin giỏ hàng thành công.");
    }

    /**
     * Xóa một mục (booking) cụ thể khỏi giỏ hàng.
     * @param itemId ID của mục cần xóa (ví dụ: ID của FlightBooking, HotelBooking).
     * @param itemType Loại của mục cần xóa (ví dụ: "FLIGHT", "HOTEL", "BUS", "TOUR").
     * @return Chi tiết giỏ hàng sau khi đã xóa mục.
     */
    @DeleteMapping("/items")
    public ResponseEntity<ApiResponse<TicketDetailDto>> removeItemFromCart(@RequestParam Integer itemId, @RequestParam String itemType) {
        TicketDetailDto updatedCart = cartService.removeItemFromCart(itemId, itemType);
        return ResponseFactory.success(updatedCart, "Xóa sản phẩm khỏi giỏ hàng thành công.");
    }
}