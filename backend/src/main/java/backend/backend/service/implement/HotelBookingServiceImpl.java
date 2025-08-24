package backend.backend.service.implement;

import backend.backend.dao.Hotel.HotelRoomVariantDAO;
import backend.backend.dao.CustomerDAO;
import backend.backend.dao.HotelBookingDAO;
import backend.backend.dao.OrderDAO;
import backend.backend.dao.UserDAO;
import backend.backend.dao.Hotel.HotelRoomDAO;
import backend.backend.dto.Hotel.HotelBookingRequestDto;
import backend.backend.dto.Hotel.UpdateHotelBookingRequestDto;
import backend.backend.dto.OrderDto;
import backend.backend.entity.Customer;
import backend.backend.entity.HotelBooking;
import backend.backend.entity.HotelRoom;
import backend.backend.entity.HotelRoomVariant;
import backend.backend.entity.Order;
import backend.backend.entity.User;
import backend.backend.service.HotelBookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import backend.backend.controller.AdminWebSocketController;
import backend.backend.service.PushNotificationService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class HotelBookingServiceImpl implements HotelBookingService {
    @Autowired
    private CustomerDAO customerDAO;
    @Autowired
    private HotelBookingDAO hotelBookingDAO;
    @Autowired
    private HotelRoomVariantDAO hotelRoomVariantDAO;
    @Autowired
    private OrderDAO orderDAO;
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private HotelRoomDAO hotelRoomDAO;
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    @Autowired
    private AdminWebSocketController adminWebSocketController;
    
    @Autowired
    private PushNotificationService pushNotificationService;

    private static final Logger log = LoggerFactory.getLogger(HotelBookingServiceImpl.class);

    @Override
    @Transactional
    public OrderDto bookHotel(HotelBookingRequestDto dto, Authentication authentication) {
        log.info("[BOOK_HOTEL] Nhận booking DTO: {}", dto);
        try {
            if (dto.getRoomVariantId() == null)
                throw new IllegalArgumentException("Thiếu roomVariantId");
            if (dto.getCheckInDate() == null || dto.getCheckOutDate() == null)
                throw new IllegalArgumentException("Thiếu ngày nhận/trả phòng");
            if (dto.getNumAdults() == null || dto.getNumChildren() == null)
                throw new IllegalArgumentException("Thiếu số lượng người");
            if (dto.getTotalPrice() == null)
                throw new IllegalArgumentException("Thiếu tổng giá");
            if (dto.getFullName() == null || dto.getEmail() == null || dto.getPhone() == null)
                throw new IllegalArgumentException("Thiếu thông tin khách hàng");
            if (dto.getRooms() == null || dto.getRooms() <= 0)
                throw new IllegalArgumentException("Thiếu hoặc sai số lượng phòng đặt");

            List<Customer> existingCustomers = customerDAO.findAllByEmail(dto.getEmail());
            Customer customer;
            
            if (!existingCustomers.isEmpty()) {
                Optional<Customer> exactMatch = existingCustomers.stream()
                    .filter(existing -> existing.getFullName().equals(dto.getFullName()) && 
                                      existing.getPhone().equals(dto.getPhone()))
                    .findFirst();
                
                if (exactMatch.isPresent()) {
                    customer = exactMatch.get();
                    log.info("[BOOK_HOTEL] Sử dụng thông tin customer hiện tại: {}", customer.getFullName());
                } else {
                    Customer newCustomer = new Customer();
                    newCustomer.setFullName(dto.getFullName());
                    newCustomer.setEmail(dto.getEmail());
                    newCustomer.setPhone(dto.getPhone());
                    customer = customerDAO.save(newCustomer);
                    log.info("[BOOK_HOTEL] Tạo customer mới (email đã tồn tại nhưng thông tin khác): {}", newCustomer.getFullName());
                }
            } else {
                Customer newCustomer = new Customer();
                newCustomer.setFullName(dto.getFullName());
                newCustomer.setEmail(dto.getEmail());
                newCustomer.setPhone(dto.getPhone());
                customer = customerDAO.save(newCustomer);
                log.info("[BOOK_HOTEL] Tạo customer mới: {}", newCustomer.getFullName());
            }

            Order order;
            if (dto.getOrderId() != null) {
                order = orderDAO.findById(dto.getOrderId())
                        .orElseThrow(
                                () -> new IllegalArgumentException("Không tìm thấy order với ID: " + dto.getOrderId()));
                BigDecimal currentAmount = order.getAmount() != null ? order.getAmount() : BigDecimal.ZERO;
                order.setAmount(currentAmount.add(dto.getTotalPrice()));
                orderDAO.save(order);
            } else {
                order = new Order();
                order.setAmount(dto.getTotalPrice());
                order.setStatus("PENDING_PAYMENT");
                order.setCreatedAt(LocalDateTime.now());
                order.setExpiresAt(order.getCreatedAt().plus(30, ChronoUnit.MINUTES));
                
                String email;
                if (authentication.getPrincipal() instanceof User) {
                    User user = (User) authentication.getPrincipal();
                    email = user.getEmail();
                } else {
                    email = authentication.getName();
                }
                
                User userEntity = userDAO.findByEmail(email).orElse(null);
                if (userEntity != null) {
                    order.setUser(userEntity);
                }
                order = orderDAO.save(order);
                log.info("[BOOK_HOTEL] Đã tạo order với ID: {}", order.getId());
            }

            HotelRoomVariant variant = hotelRoomVariantDAO.findByIdWithRoom(dto.getRoomVariantId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Không tìm thấy room variant với id: " + dto.getRoomVariantId()));
            
            if (!"APPROVED".equals(variant.getRoom().getHotel().getApprovalStatus()) || 
                !"ACTIVE".equals(variant.getRoom().getHotel().getStatus())) {
                throw new IllegalArgumentException("Khách sạn này không khả dụng để đặt phòng");
            }
            HotelBooking booking = new HotelBooking();
            booking.setRoomVariant(variant);
            booking.setCheckInDate(LocalDate.parse(dto.getCheckInDate()));
            booking.setCheckOutDate(LocalDate.parse(dto.getCheckOutDate()));
            booking.setNumAdults(dto.getNumAdults());
            booking.setNumChildren(dto.getNumChildren());
            booking.setTotalPrice(dto.getTotalPrice());
            booking.setOrder(order);
            booking.setCustomer(customer);
            booking.setCreatedAt(LocalDateTime.now());
            booking.setRooms(dto.getRooms());
            hotelBookingDAO.save(booking);
            log.info("[BOOK_HOTEL] Đã tạo hotel booking với id: {}", booking.getId());

            Short currentQty = variant.getRoom().getRoomQuantity();
            short roomsBooked = 1;
            if (dto.getRooms() != null)
                roomsBooked = dto.getRooms();
            if (currentQty == null || currentQty < roomsBooked) {
                throw new IllegalArgumentException("Số lượng phòng không đủ. Chỉ còn lại " + (currentQty == null ? 0 : currentQty) + " phòng.");
            }
            variant.getRoom().setRoomQuantity((short) (currentQty - roomsBooked));
            hotelRoomDAO.save(variant.getRoom());
            log.info("[BOOK_HOTEL] Đã trừ {} phòng, còn lại {} cho roomId={}", roomsBooked, variant.getRoom().getRoomQuantity(), variant.getRoom().getId());

            try {
                Integer hotelId = variant.getRoom().getHotel().getId();
                Map<String, Object> roomUpdate = new java.util.HashMap<>();
                roomUpdate.put("hotelId", hotelId);
                roomUpdate.put("roomId", variant.getRoom().getId());
                roomUpdate.put("previousQuantity", currentQty);
                roomUpdate.put("newQuantity", variant.getRoom().getRoomQuantity());
                roomUpdate.put("action", "ROOM_BOOKED");
                messagingTemplate.convertAndSend("/topic/hotels/" + hotelId + "/room-updates", roomUpdate);
                log.info("[BOOK_HOTEL] Đã gửi WebSocket notification cho hotelId={}, roomId={}", hotelId, variant.getRoom().getId());
                
                adminWebSocketController.sendBookingNotification(
                    customer.getFullName(),
                    variant.getRoom().getHotel().getName(),
                    dto.getRooms()
                );
                
                pushNotificationService.sendNotificationToHotelAdmins(
                    "Đặt phòng mới",
                    "Khách hàng " + customer.getFullName() + " vừa đặt " + dto.getRooms() + " phòng tại " + variant.getRoom().getHotel().getName(),
                    "/icons/booking.png",
                    "/admin/hotel/dashboard"
                );
            } catch (Exception e) {
                log.warn("[BOOK_HOTEL] Không thể gửi WebSocket notification: {}", e.getMessage());
            }

            OrderDto orderDto = new OrderDto();
            orderDto.setId(order.getId());
            orderDto.setAmount(order.getAmount());
            orderDto.setStatus(order.getStatus());
            orderDto.setCreatedAt(order.getCreatedAt());
            orderDto.setExpiresAt(order.getExpiresAt());
            return orderDto;
        } catch (Exception e) {
            log.error("[BOOK_HOTEL] Lỗi khi xử lý booking: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Transactional
    public OrderDto updateHotelBooking(UpdateHotelBookingRequestDto dto, Authentication authentication) {
        log.info("[UPDATE_HOTEL_BOOKING] Nhận update DTO: {}", dto);
        try {
            if (dto.getBookingId() == null) {
                throw new IllegalArgumentException("Thiếu bookingId");
            }
            if (dto.getNumAdults() == null || dto.getNumChildren() == null) {
                throw new IllegalArgumentException("Thiếu số lượng người");
            }
            if (dto.getRooms() == null || dto.getRooms() <= 0) {
                throw new IllegalArgumentException("Thiếu hoặc sai số lượng phòng");
            }
            if (dto.getTotalPrice() == null) {
                throw new IllegalArgumentException("Thiếu tổng giá");
            }

            HotelBooking booking = hotelBookingDAO.findById(dto.getBookingId())
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy booking với ID: " + dto.getBookingId()));
            
            HotelRoomVariant roomVariant = hotelRoomVariantDAO.findById(booking.getRoomVariant().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thông tin phòng!"));
            
            HotelRoom hotelRoom = hotelRoomDAO.findById(roomVariant.getRoom().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thông tin phòng!"));
            
            if (!"APPROVED".equals(hotelRoom.getHotel().getApprovalStatus()) || 
                !"ACTIVE".equals(hotelRoom.getHotel().getStatus())) {
                throw new IllegalArgumentException("Khách sạn này không khả dụng để cập nhật đặt phòng");
            }

            if (authentication == null) {
                throw new IllegalArgumentException("Bạn cần đăng nhập để cập nhật booking!");
            }
            
            String email;
            if (authentication.getPrincipal() instanceof User) {
                User user = (User) authentication.getPrincipal();
                email = user.getEmail();
            } else {
                email = authentication.getName();
            }
            
            User userEntity = userDAO.findByEmail(email).orElse(null);
            if (userEntity == null) {
                throw new IllegalArgumentException("Không tìm thấy thông tin người dùng!");
            }
            
            Order orderWithUser = orderDAO.findById(booking.getOrder().getId()).orElse(null);
            if (orderWithUser == null || orderWithUser.getUser() == null || orderWithUser.getUser().getId() != userEntity.getId()) {
                throw new IllegalArgumentException("Bạn không có quyền cập nhật booking này!");
            }

            if (!"PENDING_PAYMENT".equals(orderWithUser.getStatus())) {
                throw new IllegalArgumentException("Chỉ có thể cập nhật booking khi đơn hàng đang chờ thanh toán!");
            }
            Short oldRooms = booking.getRooms();
            Short newRooms = dto.getRooms();
            int roomDifference = newRooms - oldRooms;

            if (roomDifference > 0) {
                if (hotelRoom.getRoomQuantity() < roomDifference) {
                    throw new IllegalArgumentException("Không đủ phòng để tăng số lượng. Chỉ còn lại " + hotelRoom.getRoomQuantity() + " phòng.");
                }
                hotelRoom.setRoomQuantity((short) (hotelRoom.getRoomQuantity() - roomDifference));
                hotelRoomDAO.save(hotelRoom);
            } else if (roomDifference < 0) {
                hotelRoom.setRoomQuantity((short) (hotelRoom.getRoomQuantity() + Math.abs(roomDifference)));
                hotelRoomDAO.save(hotelRoom);
            }

            booking.setNumAdults(dto.getNumAdults());
            booking.setNumChildren(dto.getNumChildren());
            booking.setRooms(dto.getRooms());
            booking.setTotalPrice(dto.getTotalPrice());
            hotelBookingDAO.save(booking);

            List<HotelBooking> hotelBookings = hotelBookingDAO.findByOrderId(orderWithUser.getId());
            BigDecimal newTotalAmount = hotelBookings.stream()
                    .map(HotelBooking::getTotalPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            orderWithUser.setAmount(newTotalAmount);
            orderDAO.save(orderWithUser);

            log.info("[UPDATE_HOTEL_BOOKING] Đã cập nhật booking với id: {}", booking.getId());

            try {
                Integer hotelId = hotelRoom.getHotel().getId();
                Map<String, Object> roomUpdate = new java.util.HashMap<>();
                roomUpdate.put("hotelId", hotelId);
                roomUpdate.put("roomId", hotelRoom.getId());
                roomUpdate.put("previousQuantity", hotelRoom.getRoomQuantity() + (roomDifference > 0 ? roomDifference : -roomDifference));
                roomUpdate.put("newQuantity", hotelRoom.getRoomQuantity());
                roomUpdate.put("action", roomDifference > 0 ? "ROOM_BOOKED" : "ROOM_RESTORED");
                messagingTemplate.convertAndSend("/topic/hotels/" + hotelId + "/room-updates", roomUpdate);
                log.info("[UPDATE_HOTEL_BOOKING] Đã gửi WebSocket notification cho hotelId={}, roomId={}", hotelId, hotelRoom.getId());
            } catch (Exception e) {
                log.warn("[UPDATE_HOTEL_BOOKING] Không thể gửi WebSocket notification: {}", e.getMessage());
            }

            OrderDto orderDto = new OrderDto();
            orderDto.setId(orderWithUser.getId());
            orderDto.setAmount(orderWithUser.getAmount());
            orderDto.setStatus(orderWithUser.getStatus());
            orderDto.setCreatedAt(orderWithUser.getCreatedAt());
            orderDto.setExpiresAt(orderWithUser.getExpiresAt());
            return orderDto;

        } catch (Exception e) {
            log.error("[UPDATE_HOTEL_BOOKING] Lỗi khi cập nhật booking: {}", e.getMessage(), e);
            throw e;
        }
    }
}