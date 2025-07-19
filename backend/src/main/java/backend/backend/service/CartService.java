package backend.backend.service;

import backend.backend.dto.TicketDetailDto;

public interface CartService {
	
	 TicketDetailDto createCartForUser(Integer userId);
	    TicketDetailDto getCartById(Integer cartId);
	    TicketDetailDto removeItemFromCart(Integer itemId, String itemType);
}
