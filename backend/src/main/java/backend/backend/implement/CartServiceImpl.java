package backend.backend.implement;

import backend.backend.dao.*;
import backend.backend.dto.*;
import backend.backend.entity.*;
import backend.backend.exception.ResourceNotFoundException;
import backend.backend.mapper.OrderMapper;
import backend.backend.service.BookingTourService;
import backend.backend.service.CartService;
import backend.backend.entity.VoucherType; 

import backend.backend.service.FlightBookingService;

import backend.backend.service.HotelBookingService;
import backend.backend.dto.Hotel.HotelBookingRequestDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.stream.Stream;

@Service
public class CartServiceImpl implements CartService {

    @Autowired private OrderDAO orderDAO;
    @Autowired private UserDAO userDAO;
    @Autowired private FlightBookingDAO flightBookingDAO;
    @Autowired private HotelBookingDAO hotelBookingDAO;
    @Autowired private BusBookingDAO busBookingDAO;
    @Autowired private BookingTourDAO bookingTourDAO;
    @Autowired private  BookingTourService tourBookingService;
    @Autowired private FlightBookingService flightBookingService;
    @Autowired
    private HotelBookingService hotelBookingService;
    @Autowired
    private OrderMapper orderMapper; 
   

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
        if (!"CART".equalsIgnoreCase(cart.getStatus()) && !"PENDING_PAYMENT".equalsIgnoreCase(cart.getStatus())) {
            throw new IllegalStateException("Order này không phải là giỏ hàng.");
        }
        return toOrderDto(cart);
    }

  

   
   

    @Override
    @Transactional
    public OrderDto removeItemFromCart(Integer itemId, String itemType) {
        Order orderToUpdate;

        // 1. Tìm và xóa mục khỏi giỏ hàng
        switch (itemType.toUpperCase()) {
            case "FLIGHT": {
                FlightBooking flight = flightBookingDAO.findById(itemId)
                        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy vé máy bay với ID: " + itemId));
                orderToUpdate = flight.getOrder();
                orderToUpdate.getFlightBookings().remove(flight);
                flightBookingDAO.delete(flight);
                break;
            }
            case "HOTEL": {
                HotelBooking hotel = hotelBookingDAO.findById(itemId)
                        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đặt phòng khách sạn với ID: " + itemId));
                orderToUpdate = hotel.getOrder();
                orderToUpdate.getHotelBookings().remove(hotel);
                if (hotel.getRoomVariant() != null && hotel.getRooms() != null && hotel.getRooms() > 0) {
                    var room = hotel.getRoomVariant().getRoom();
                    if (room != null && room.getRoomQuantity() != null) {
                        room.setRoomQuantity((short) (room.getRoomQuantity() + hotel.getRooms()));
                    }
                 }
                hotelBookingDAO.delete(hotel);
                break;
            }
            case "BUS": {
                BusBooking bus = busBookingDAO.findById(itemId)
                        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy vé xe buýt với ID: " + itemId));
                orderToUpdate = bus.getOrder();
                orderToUpdate.getBusBookings().remove(bus);
                busBookingDAO.delete(bus);
                break;
            }
            case "TOUR": {
                BookingTour tour = bookingTourDAO.findById(itemId)
                        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đặt tour với ID: " + itemId));
                orderToUpdate = tour.getOrder();
                orderToUpdate.getBookingTours().remove(tour);
                bookingTourDAO.delete(tour);
                break;
            }
            default:
                throw new IllegalArgumentException("Loại sản phẩm không hợp lệ: " + itemType);
        }

        // 2. Gọi phương thức helper để tính toán lại toàn bộ chi tiết đơn hàng
        recalculateOrderDetails(orderToUpdate);

        // 3. Quyết định và trả về DTO phù hợp dựa trên trạng thái mới của đơn hàng
        if ("CANCELLED".equals(orderToUpdate.getStatus())) {
            // Nếu đơn hàng đã bị hủy (do rỗng), trả về DTO của nó ngay lập tức
            return orderMapper.toDto(orderToUpdate);
        }
        
        // Nếu đơn hàng vẫn là giỏ hàng hợp lệ, gọi getCartById để lấy thông tin mới nhất
        return getCartById(orderToUpdate.getId());
    }

    /**
     * Phương thức private để tính toán và cập nhật lại toàn bộ chi tiết cho Order.
     * Phương thức này chỉ cập nhật đối tượng Order, không trả về giá trị (void).
     * @param order Đối tượng Order cần được cập nhật.
     */
    private void recalculateOrderDetails(Order order) {
        // --- 1. TÍNH TOÁN LẠI TỔNG PHỤ (SUBTOTAL) ---
        BigDecimal subTotal = Stream.of(
                order.getFlightBookings().stream().map(FlightBooking::getTotalPrice),
                order.getHotelBookings().stream().map(HotelBooking::getTotalPrice),
                order.getBusBookings().stream().map(BusBooking::getTotalPrice),
                order.getBookingTours().stream().map(BookingTour::getTotalPrice)
            )
            .flatMap(s -> s)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        // --- 2. KIỂM TRA VÀ CẬP NHẬT VOUCHER ---
        Voucher voucher = order.getVoucher();
        BigDecimal discountAmount = BigDecimal.ZERO;

//        if (voucher != null) {
//            // Nếu tổng phụ nhỏ hơn mức chi tiêu tối thiểu của voucher, hãy loại bỏ nó.
//            // Giả sử voucher.getConditionMinAmount() tồn tại
//            if (subTotal.compareTo(voucher.getConditionMinAmount()) < 0) {
//                order.setVoucher(null); 
//            } else {
//                // Nếu voucher vẫn hợp lệ, tính toán lại số tiền giảm giá
//                if ("PERCENTAGE".equals(voucher.getDiscountType())) {
//                    discountAmount = subTotal.multiply(voucher.getDiscountAmount().divide(new BigDecimal("100")));
//                    if (discountAmount.compareTo(voucher.getMaxDiscount()) > 0) {
//                        discountAmount = voucher.getMaxDiscount();
//                    }
//                } else if ("FIXED".equals(voucher.getDiscountType())) {
//                    // Chuyển đổi getDiscountAmount() sang BigDecimal nếu cần
//                    discountAmount = BigDecimal.valueOf(voucher.getDiscountAmount());
//                }
//            }
//        }

        // --- 3. CẬP NHẬT SỐ TIỀN THANH TOÁN CUỐI CÙNG (amount) ---
        BigDecimal finalAmount = subTotal.subtract(discountAmount);
        order.setAmount(finalAmount.max(BigDecimal.ZERO));

        // --- 4. CẬP NHẬT TRẠNG THÁI (status) ---
        // Nếu giỏ hàng trống, cập nhật trạng thái đã hủy
        if (subTotal.compareTo(BigDecimal.ZERO) == 0) {
            order.setStatus("CANCELLED");
        }
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

    @Override
    @Transactional
    public OrderDto addItemToCart(Integer orderId, AddItemRequestDto genericRequest) {
      
        // 1. Lấy đối tượng Order. Đây là một "managed entity".
        Order order = orderDAO.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giỏ hàng với ID: " + orderId));

        // 2. Gọi service con để cập nhật
        switch (genericRequest.getItemType()) {
            case TOUR:
                BookingTourRequestDto tourRequest = mapToTourRequest(order, genericRequest);
               
                tourBookingService.createBookingTour(tourRequest);
                break;
            case BUS:
                 break;
      
            case HOTEL: {
                HotelBookingRequestDto hotelRequest = new HotelBookingRequestDto();
                User user = order.getUser();
                hotelRequest.setFullName(user != null ? user.getName() : "");
                hotelRequest.setEmail(user != null ? user.getEmail() : "");
                hotelRequest.setPhone(user != null ? user.getPhone() : "");
                hotelRequest.setRoomVariantId(genericRequest.getRoomId());
                hotelRequest.setCheckInDate(genericRequest.getCheckInDate() != null ? genericRequest.getCheckInDate().toString() : null);
                hotelRequest.setCheckOutDate(genericRequest.getCheckOutDate() != null ? genericRequest.getCheckOutDate().toString() : null);
                hotelRequest.setNumAdults((short) genericRequest.getNumberOfAdults());
                hotelRequest.setNumChildren((short) genericRequest.getNumberOfChildren());
                hotelRequest.setTotalPrice(genericRequest.getTotalPrice());
                hotelRequest.setRooms(genericRequest.getNumberOfRooms() != null ? genericRequest.getNumberOfRooms().shortValue() : 1);
                hotelRequest.setOrderId(order.getId());
                hotelBookingService.bookHotel(hotelRequest, null);
                break;
            }
        
            case FLIGHT:
                flightBookingService.createFlightBooking(orderId, genericRequest);

                 break;
            
            default:
                throw new IllegalArgumentException("Loại dịch vụ không được hỗ trợ: " + genericRequest.getItemType());
        }
      
       
        return orderMapper.toDto(order); 
    }

    private BookingTourRequestDto mapToTourRequest(Order order, AddItemRequestDto genericRequest) {
        BookingTourRequestDto tourRequest = new BookingTourRequestDto();
        tourRequest.setUserId(order.getUser().getId());
        tourRequest.setOrderId(order.getId());
        tourRequest.setTourId(genericRequest.getItemId());
        tourRequest.setDepartureId(genericRequest.getDepartureId());
        tourRequest.setNumberOfAdults(genericRequest.getNumberOfAdults());
        tourRequest.setNumberOfChildren(genericRequest.getNumberOfChildren());
        return tourRequest;
    }
    
}