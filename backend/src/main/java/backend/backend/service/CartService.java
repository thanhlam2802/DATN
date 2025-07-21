package backend.backend.service;

import backend.backend.dto.AddItemRequestDto;
import backend.backend.dto.OrderDto;

public interface CartService {
    OrderDto createCartForUser(Integer userId);
    OrderDto getCartById(Integer orderId);
    OrderDto removeItemFromCart(Integer itemId, String itemType);
    
    /**
     * Thêm một mục mới vào giỏ hàng (Order) dựa trên thông tin yêu cầu.
     * Phương thức này sẽ điều phối đến các service chuyên dụng khác nhau (Tour, Hotel...)
     * dựa vào `itemType` trong request.
     *
     * @param orderId ID của giỏ hàng cần cập nhật.
     * @param request DTO chứa thông tin về mục cần thêm.
     * @return OrderDto chi tiết giỏ hàng sau khi đã được cập nhật.
     */
    OrderDto addItemToCart(Integer orderId, AddItemRequestDto request);
}
