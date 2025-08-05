package backend.backend.implement;

import backend.backend.dao.*;
import backend.backend.dto.*;
import backend.backend.dto.BusDTO.BusBookingRequest;
import backend.backend.entity.*;
import backend.backend.exception.ResourceNotFoundException;
import backend.backend.mapper.OrderMapper;
import backend.backend.service.BookingTourService;
import backend.backend.service.CartService;
import backend.backend.entity.VoucherType; 

import backend.backend.service.FlightBookingService;

import backend.backend.service.HotelBookingService;
import backend.backend.dto.Hotel.HotelBookingRequestDto;

import backend.backend.service.busService.BusBookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Log4j2
public class CartServiceImpl implements CartService {

     private  final OrderDAO orderDAO;
     private  final UserDAO userDAO;
     private  final FlightBookingDAO flightBookingDAO;
     private  final HotelBookingDAO hotelBookingDAO;
     private  final BusBookingDAO busBookingDAO;
     private  final BookingTourDAO bookingTourDAO;
     private  final  BookingTourService tourBookingService;
     private  final FlightBookingService flightBookingService;
     private  final BusBookingService busBookingService; // ← ADD DEPENDENCY
    private final CustomerDAO customerDAO;
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
        cart.setAmount(BigDecimal.ZERO);
        cart.setCreatedAt(LocalDateTime.now());
        // ✅ FIX: Set expiry far in future so cart never gets auto-cancelled
        cart.setExpiresAt(LocalDateTime.now().plusDays(30)); // 30 ngày
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
//                BusBooking bus = busBookingDAO.findById(itemId)
//                        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy vé xe buýt với ID: " + itemId));
//                orderToUpdate = bus.getOrder();
//                orderToUpdate.getBusBookings().remove(bus);
//                busBookingDAO.delete(bus);
//                break;
                BusBooking bus = busBookingDAO.findById(itemId)
                        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy vé xe buýt với ID: " + itemId));
                orderToUpdate = bus.getOrder();

                // ✅ COMMENTED OUT: Frontend already calls cancel API before removing from cart
                // busBookingService.cancelBusBooking(bus.getId());

                // Remove from order's collection
                if (orderToUpdate.getBusBookings() != null) {
                    orderToUpdate.getBusBookings().remove(bus);
                }
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
        // ✅ DEBUG: Add logging to understand what's happening
        log.debug("Recalculating order details for order {}", order.getId());
        log.debug("Flight bookings: {}", order.getFlightBookings().size());
        log.debug("Hotel bookings: {}", order.getHotelBookings().size());
        log.debug("Bus bookings: {}", order.getBusBookings().size());
        log.debug("Tour bookings: {}", order.getBookingTours().size());

        BigDecimal subTotal = Stream.of(
                        order.getFlightBookings().stream().map(FlightBooking::getTotalPrice),
                        order.getHotelBookings().stream().map(HotelBooking::getTotalPrice),
                        order.getBusBookings().stream().map(BusBooking::getTotalPrice),
                        order.getBookingTours().stream().map(BookingTour::getTotalPrice)
                )
                .flatMap(s -> s)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        log.debug("Calculated subTotal: {}", subTotal);

        // --- 2. KIỂM TRA VÀ CẬP NHẬT VOUCHER ---
        Voucher voucher = order.getVoucher();
        BigDecimal discountAmount = BigDecimal.ZERO;

        // --- 3. CẬP NHẬT SỐ TIỀN THANH TOÁN CUỐI CÙNG (amount) ---
        BigDecimal finalAmount = subTotal.subtract(discountAmount);
        order.setAmount(finalAmount.max(BigDecimal.ZERO));

        log.debug("Final amount: {}", order.getAmount());

        // --- 4. CẬP NHẬT TRẠNG THÁI (status) ---
        // Nếu giỏ hàng trống, cập nhật trạng thái đã hủy
        if (subTotal.compareTo(BigDecimal.ZERO) == 0) {
            log.warn("Order {} has zero subtotal, marking as CANCELLED", order.getId());
            order.setStatus("CANCELLED");
        } else {
            log.debug("Order {} subtotal > 0, keeping current status: {}", order.getId(), order.getStatus());
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
            {
                BusBookingRequest busRequest = mapToBusRequest(order, genericRequest);
                BusBooking busBooking = busBookingService.createBusBookingForCart(busRequest);


                busBooking.setOrder(order);
                busBookingDAO.save(busBooking);
                break;
            }
      
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
        // ✅ 3. QUAN TRỌNG: TẢI LẠI ORDER (FIX MultipleBagFetchException)
        Order refreshedOrder = orderDAO.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giỏ hàng sau khi cập nhật: " + orderId));

        // ✅ Trigger lazy loading for collections (safer approach)
        refreshedOrder.getBusBookings().size();        // Trigger bus bookings load
        refreshedOrder.getFlightBookings().size();     // Trigger flight bookings load  
        refreshedOrder.getHotelBookings().size();      // Trigger hotel bookings load
        refreshedOrder.getBookingTours().size();       // Trigger tour bookings load

        // ✅ 4. TÍNH TOÁN LẠI VỚI DỮLIỆU MỚI
        recalculateOrderDetails(refreshedOrder);

        // ✅ 5. LƯU VÀ TRẢ VỀ KẾT QUẢ
        Order savedOrder = orderDAO.save(refreshedOrder);
        log.info("✅ Cart updated successfully - Order ID: {}, Status: {}, Amount: {}", 
         savedOrder.getId(), savedOrder.getStatus(), savedOrder.getAmount());
         return orderMapper.toDto(savedOrder);
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

    private BusBookingRequest mapToBusRequest(Order order, AddItemRequestDto genericRequest) {
        BusBookingRequest busRequest = new BusBookingRequest();
        busRequest.setBusSlotId(genericRequest.getItemId().intValue());

        // ✅ FIX: Get or create customer from current user context
        Integer customerId = getOrCreateCustomerFromContext(genericRequest);
        busRequest.setCustomerId(customerId);

        busRequest.setSelectedSeatIds(genericRequest.getSelectedSeatIds());
        busRequest.setTotalPrice(genericRequest.getTotalPrice());
        busRequest.setNotes(genericRequest.getNotes());

        // Extract passenger info
        busRequest.setPassengerName(genericRequest.getPassengerName());
        busRequest.setPassengerPhone(genericRequest.getPassengerPhone());
        busRequest.setPassengerEmail(genericRequest.getPassengerEmail());

        return busRequest;
    }

    private Integer getOrCreateCustomerFromContext(AddItemRequestDto request) {
        try {
            // ✅ Method 1: Use existing customerId if provided
            if (request.getCustomerId() != null) {
                return request.getCustomerId();
            }

            // ✅ Method 2: Find existing customer by phone HOẶC email
            Optional<Customer> existingCustomer = Optional.empty();

            // Tìm theo phone trước
            if (request.getPassengerPhone() != null && !request.getPassengerPhone().trim().isEmpty()) {
                existingCustomer = customerDAO.findByPhone(request.getPassengerPhone());
            }

            // Nếu không tìm thấy theo phone, tìm theo email
            if (existingCustomer.isEmpty() &&
                    request.getPassengerEmail() != null &&
                    !request.getPassengerEmail().trim().isEmpty()) {
                existingCustomer = customerDAO.findByEmail(request.getPassengerEmail());
            }

            if (existingCustomer.isPresent()) {
                return existingCustomer.get().getId();
            }

            // ✅ Method 3: Create new customer from passenger info
            Customer newCustomer = new Customer();
            newCustomer.setFullName(request.getPassengerName());
            newCustomer.setPhone(request.getPassengerPhone());
            newCustomer.setEmail(request.getPassengerEmail());

            Customer savedCustomer = customerDAO.save(newCustomer);
            return savedCustomer.getId();

        } catch (Exception e) {
            // ✅ Fallback: Create customer from passenger info
            Customer fallbackCustomer = new Customer();
            fallbackCustomer.setFullName(request.getPassengerName() != null ? request.getPassengerName() : "Khách hàng");
            fallbackCustomer.setPhone(request.getPassengerPhone());
            fallbackCustomer.setEmail(request.getPassengerEmail());

            Customer savedCustomer = customerDAO.save(fallbackCustomer);
            return savedCustomer.getId();
        }
    }
}