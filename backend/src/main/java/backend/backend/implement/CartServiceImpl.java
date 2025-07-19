package backend.backend.implement;

import backend.backend.dao.*;
import backend.backend.dto.*;
import backend.backend.entity.*;
import backend.backend.exception.ResourceNotFoundException;
import backend.backend.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {

    @Autowired private OrderDAO orderDAO;
    @Autowired private UserDAO userDAO;
    @Autowired private FlightBookingDAO flightBookingDAO;
    @Autowired private HotelBookingDAO hotelBookingDAO;
    @Autowired private BusBookingDAO busBookingDAO;
    @Autowired private BookingTourDAO bookingTourDAO;

    @Override
    @Transactional
    public OrderDto createCartForUser(Integer userId) {
        User user = userDAO.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với ID: " + userId));
        // Kiểm tra đã có cart chưa
        Order existingCart = orderDAO.findFirstByUserIdAndStatus(userId, "CART");
        if (existingCart != null) {
            return toOrderDto(existingCart);
        }
        Order cart = new Order();
        cart.setUser(user);
        cart.setStatus("CART");
        Order saved = orderDAO.save(cart);
        return toOrderDto(saved);
    }

    @Override
    public OrderDto getCartById(Integer orderId) {
        Order cart = orderDAO.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giỏ hàng với ID: " + orderId));
        if (!"CART".equalsIgnoreCase(cart.getStatus())) {
            throw new IllegalStateException("Order này không phải là giỏ hàng.");
        }
        return toOrderDto(cart);
    }

    @Override
    @Transactional
    public OrderDto removeItemFromCart(Integer itemId, String itemType) {
        Integer orderId = null;
        switch (itemType.toUpperCase()) {
            case "FLIGHT":
                FlightBooking flight = flightBookingDAO.findById(itemId)
                        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy vé máy bay với ID: " + itemId));
                orderId = flight.getOrder().getId();
                flightBookingDAO.delete(flight);
                break;
            case "HOTEL":
                HotelBooking hotel = hotelBookingDAO.findById(itemId)
                        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đặt phòng khách sạn với ID: " + itemId));
                orderId = hotel.getOrder().getId();
                hotelBookingDAO.delete(hotel);
                break;
            case "BUS":
                BusBooking bus = busBookingDAO.findById(itemId)
                        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy vé xe buýt với ID: " + itemId));
                orderId = bus.getOrder().getId();
                busBookingDAO.delete(bus);
                break;
            case "TOUR":
                BookingTour tour = bookingTourDAO.findById(itemId)
                        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đặt tour với ID: " + itemId));
                orderId = tour.getOrder().getId();
                bookingTourDAO.delete(tour);
                break;
            default:
                throw new IllegalArgumentException("Loại sản phẩm không hợp lệ: " + itemType);
        }
        return getCartById(orderId);
    }

    // Helper: convert Order entity to OrderDto (bạn cần hoàn thiện mapping booking nếu muốn trả về booking list)
    private OrderDto toOrderDto(Order entity) {
        OrderDto dto = new OrderDto();
        dto.setId(entity.getId());
        dto.setStatus(entity.getStatus());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUserId(entity.getUser() != null ? entity.getUser().getId() : null);
        // Mapping các booking nếu cần
        // ...
        return dto;
    }
     // viết thêm cái của minh vao nhan mấy ông 

    private FlightBookingDto toFlightBookingDTO(FlightBooking entity) {
        FlightBookingDto dto = new FlightBookingDto();
      
        return dto;
    }

    private HotelBookingDto toHotelBookingDTO(HotelBooking entity) {
        HotelBookingDto dto = new HotelBookingDto();
        return dto;
    }
}