package backend.backend.service;

import backend.backend.dto.OrderDto;

public interface CartService {
    OrderDto createCartForUser(Integer userId);
    OrderDto getCartById(Integer orderId);
    OrderDto removeItemFromCart(Integer itemId, String itemType);
}
