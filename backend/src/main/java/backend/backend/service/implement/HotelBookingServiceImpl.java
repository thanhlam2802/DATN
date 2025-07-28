package backend.backend.service.implement;

import backend.backend.dao.Hotel.HotelRoomVariantDAO;
import backend.backend.dao.CustomerDAO;
import backend.backend.dao.HotelBookingDAO;
import backend.backend.dao.OrderDAO;
import backend.backend.dao.UserDAO;
import backend.backend.dao.Hotel.HotelRoomDAO;
import backend.backend.dto.Hotel.HotelBookingRequestDto;
import backend.backend.dto.OrderDto;
import backend.backend.entity.Customer;
import backend.backend.entity.HotelBooking;
import backend.backend.entity.HotelRoomVariant;
import backend.backend.entity.Order;
import backend.backend.entity.User;
import backend.backend.service.HotelBookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;

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

    private static final Logger log = LoggerFactory.getLogger(HotelBookingServiceImpl.class);

    @Override
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

            Customer customer = customerDAO.findByEmail(dto.getEmail())
                    .orElseGet(() -> {
                        Customer c = new Customer();
                        c.setFullName(dto.getFullName());
                        c.setEmail(dto.getEmail());
                        c.setPhone(dto.getPhone());
                        log.info("[BOOK_HOTEL] Tạo customer mới: {}", c);
                        return customerDAO.save(c);
                    });

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
                if (authentication == null) {
                    User defaultUser = userDAO.findById(1).orElse(null);
                    if (defaultUser != null) {
                        order.setUser(defaultUser);
                    }
                } else {
                    String email = authentication.getName();
                    User user = userDAO.findByEmail(email).orElse(null);
                    if (user != null) {
                        order.setUser(user);
                    }
                }
                order = orderDAO.save(order);
                log.info("[BOOK_HOTEL] Đã tạo order: {}", order);
            }

            HotelRoomVariant variant = hotelRoomVariantDAO.findByIdWithRoom(dto.getRoomVariantId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Không tìm thấy room variant với id: " + dto.getRoomVariantId()));
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
}