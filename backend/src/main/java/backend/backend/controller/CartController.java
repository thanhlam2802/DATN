package backend.backend.controller;

import backend.backend.dto.AddItemRequestDto;
import backend.backend.dto.OrderDto;
import backend.backend.entity.ApiResponse;
import backend.backend.service.CartService;
import backend.backend.utils.ResponseFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart")
public class CartController {

    private static final Logger logger = LoggerFactory.getLogger(CartController.class);

    @Autowired
    private CartService cartService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<OrderDto>> createCart(@RequestParam Integer userId) {
        logger.info("Yêu cầu tạo giỏ hàng cho userId={}", userId);
        OrderDto newCart = cartService.createCartForUser(userId);
        logger.debug("Giỏ hàng mới được tạo: {}", newCart);
        return ResponseFactory.created(newCart, "Tạo giỏ hàng mới thành công.");
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderDto>> getCartDetails(@PathVariable Integer orderId) {
        logger.info("Yêu cầu lấy chi tiết giỏ hàng với orderId={}", orderId);
        OrderDto cart = cartService.getCartById(orderId);
        logger.debug("Chi tiết giỏ hàng: {}", cart);
        return ResponseFactory.success(cart, "Lấy thông tin giỏ hàng thành công."); // ✅ ADD THIS LINE
    }
    @PostMapping("/{orderId}/items")
    public ResponseEntity<ApiResponse<OrderDto>> addItemToCart(
            @PathVariable Integer orderId,
            @RequestBody AddItemRequestDto request) {
        logger.info("Yêu cầu thêm item vào giỏ hàng orderId={}, request={}", orderId, request);
        OrderDto updatedCart = cartService.addItemToCart(orderId, request);
        logger.debug("Giỏ hàng sau khi thêm item: {}", updatedCart);
        return ResponseFactory.success(updatedCart, "Thêm dịch vụ vào giỏ hàng thành công.");
    }

    @DeleteMapping("/items")
    public ResponseEntity<ApiResponse<OrderDto>> removeItemFromCart(@RequestParam Integer itemId, @RequestParam String itemType) {
        logger.info("Yêu cầu xóa item khỏi giỏ hàng itemId={}, itemType={}", itemId, itemType);
        OrderDto updatedCart = cartService.removeItemFromCart(itemId, itemType);
        logger.debug("Giỏ hàng sau khi xóa item: {}", updatedCart);
        return ResponseFactory.success(updatedCart, "Xóa sản phẩm khỏi giỏ hàng thành công.");
    }
}
