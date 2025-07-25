package backend.backend.service;

import java.util.List;

import backend.backend.dto.CheckoutDto;
import backend.backend.dto.DirectFlightReservationRequestDto;
import backend.backend.dto.DirectTourReservationRequestDto;
import backend.backend.dto.OrderDto;



public interface OrderService {
	
	OrderDto placeOrder(CheckoutDto checkoutDto);
	OrderDto getOrderById(Integer id);
	OrderDto createDirectTourReservation(DirectTourReservationRequestDto directRequest);
	Integer createDirectFlightReservation(DirectFlightReservationRequestDto directRequest);
	/**
     * Lấy danh sách các đơn hàng của một người dùng.
     * @param userId ID của người dùng.
     * @return Một danh sách các OrderDto.
     */
    List<OrderDto> getOrdersByUserId(Integer userId);

	OrderDto paidOrder(Integer id,String transactionId);
	
	OrderDto applyVoucherToOrder(Integer orderId, String voucherCode);
	
}
