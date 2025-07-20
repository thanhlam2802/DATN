package backend.backend.implement;

import backend.backend.dao.*;
import backend.backend.dto.CheckoutDto;
import backend.backend.dto.DirectFlightReservationRequestDto;
import backend.backend.dto.DirectTourReservationRequestDto;
import backend.backend.dto.OrderDto;
import backend.backend.entity.*;
import backend.backend.exception.ResourceNotFoundException;
import backend.backend.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    // --- CÁC DAO CẦN THIẾT ---
    @Autowired private OrderDAO orderDAO;
    @Autowired private VoucherDAO voucherDAO;
    @Autowired private UserDAO userDAO;
    @Autowired private DepartureDAO departureDAO;
    @Autowired private BookingTourDAO bookingTourDAO;
    @Autowired private TourDAO tourDAO;
    @Autowired private FlightSlotDAO flightSlotDAO;
    @Autowired private FlightBookingDAO flightBookingDAO;
    @Autowired private CustomerDAO customerDAO;
    @Autowired private HotelBookingDAO hotelBookingDAO;

    @Override
    @Transactional
    public OrderDto placeOrder(CheckoutDto checkoutDto) {
        // 1. Lấy Order theo orderId
        Order order = orderDAO.findById(checkoutDto.getOrderId())
            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng với ID: " + checkoutDto.getOrderId()));

        // 2. Kiểm tra trạng thái order (chỉ cho phép thanh toán nếu đang ở trạng thái CART hoặc PENDING)
        if (!"CART".equalsIgnoreCase(order.getStatus()) && !"PENDING".equalsIgnoreCase(order.getStatus())) {
            throw new IllegalStateException("Đơn hàng này đã được xử lý hoặc không hợp lệ.");
        }

        // 3. Áp dụng voucher nếu có
        if (checkoutDto.getVoucherId() != null) {
            Voucher voucher = voucherDAO.findById(checkoutDto.getVoucherId())
                .orElseThrow(() -> new ResourceNotFoundException("Voucher không hợp lệ."));
            order.setVoucher(voucher);
        }

        // 4. Cập nhật trạng thái, ngày thanh toán
        order.setStatus("PAID");
        order.setPayDate(LocalDateTime.now());
        // Có thể cập nhật thêm paymentMethod, paymentDetails nếu cần

        orderDAO.save(order);

        return toOrderDTO(order);
    }
    
    @Override
    public OrderDto getOrderById(Integer id) {
        Order order = orderDAO.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hóa đơn với ID: " + id));
        // SỬA LỖI: Gọi đúng mapper
        return toOrderDTO(order);
    }

    @Override
    @Transactional
    public OrderDto createDirectTourReservation(DirectTourReservationRequestDto directRequest) {
        User user = userDAO.findById(directRequest.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với ID: " + directRequest.getUserId()));
        
        Departure departure = departureDAO.findById(directRequest.getDepartureId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy ngày khởi hành với ID: " + directRequest.getDepartureId()));

        BigDecimal adultPrice = departure.getAdultPrice();
        BigDecimal childPrice = departure.getChildPrice();
        BigDecimal totalAdultPrice = adultPrice.multiply(new BigDecimal(directRequest.getNumberOfAdults()));
        BigDecimal totalChildPrice = childPrice.multiply(new BigDecimal(directRequest.getNumberOfChildren()));
        BigDecimal totalPrice = totalAdultPrice.add(totalChildPrice);

        // Tạo Order mới
        Order temporaryOrder = new Order();
        temporaryOrder.setUser(user);
        temporaryOrder.setAmount(totalPrice);
        temporaryOrder.setStatus("PENDING_PAYMENT");
        temporaryOrder.setExpiresAt(LocalDateTime.now().plusMinutes(30));

        Order savedOrder = orderDAO.save(temporaryOrder);

        // Tạo BookingTour liên kết trực tiếp với Order
        BookingTour bookingTour = new BookingTour();
        bookingTour.setDeparture(departure);
        bookingTour.setOrder(savedOrder);
        bookingTour.setCustomerName(directRequest.getCustomerName());
        bookingTour.setPhone(directRequest.getPhone());
        bookingTour.setNumberOfAdults(directRequest.getNumberOfAdults());
        bookingTour.setNumberOfChildren(directRequest.getNumberOfChildren());
        bookingTour.setNotes(directRequest.getNotes());
        bookingTour.setTotalPrice(totalPrice);
        bookingTour.setBookingDate(LocalDate.now());
        bookingTourDAO.save(bookingTour);

        return toOrderDTO(savedOrder);
    }

    @Override
    @Transactional
    public OrderDto createDirectFlightReservation(DirectFlightReservationRequestDto directRequest) {
        // 1. Lấy user từ context (chuẩn):
        // String username = SecurityContextHolder.getContext().getAuthentication().getName();
        // User user = userDAO.findByUsername(username).orElseThrow(...);
        // Tạm thời hardcode:
        User user = userDAO.findById(1)
            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy user."));

        // 2. Lấy slot và flight
        FlightSlot slot = flightSlotDAO.findById(directRequest.getFlightSlotId())
            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy slot ghế."));
        Flight flight = slot.getFlight();

        // 3. Kiểm tra slot đã được đặt chưa
        boolean slotBooked = flightBookingDAO.findByFlightSlotId(slot.getId()).size() > 0;
        if (slotBooked) {
            throw new IllegalStateException("Vé này đã có người khác đặt. Bạn đã thao tác chậm, vui lòng chọn vé khác!");
        }

        // 4. Tính tổng tiền
        BigDecimal totalPrice = slot.getPrice();

        // 5. Tính expiresAt
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime departureTime = flight.getDepartureTime();
        LocalDateTime expiresAt = now.plusMinutes(30);
        if (departureTime.isBefore(now.plusMinutes(30))) {
            expiresAt = departureTime;
        }

        // 6. Tạo order
        Order order = new Order();
        order.setUser(user);
        order.setAmount(totalPrice);
        order.setStatus("PENDING_PAYMENT");
        order.setExpiresAt(expiresAt);
        order.setCreatedAt(now);
        Order savedOrder = orderDAO.save(order);

        // 7. Lưu thông tin khách hàng
        Customer customer = new Customer();
        customer.setFullName(directRequest.getCustomerName());
        customer.setPhone(directRequest.getPhone());
        customer.setEmail(directRequest.getEmail());
        customer.setPassport(directRequest.getPassport());
        customer.setGender("male".equalsIgnoreCase(directRequest.getGender()));
        if (directRequest.getDob() != null && !directRequest.getDob().isEmpty()) {
            customer.setDob(java.time.LocalDate.parse(directRequest.getDob()));
        }
        Customer savedCustomer = customerDAO.save(customer);

        // 8. Tạo booking flight
        FlightBooking booking = new FlightBooking();
        booking.setFlightSlot(slot);
        booking.setOrder(savedOrder);
        booking.setBookingDate(now);
        booking.setTotalPrice(totalPrice);
        booking.setCustomer(savedCustomer);
        flightBookingDAO.save(booking);

        return toOrderDTO(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDto> getOrdersByUserId(Integer userId) {
        List<Order> orders = orderDAO.findByUserIdOrderByCreatedAtDesc(userId);
        return orders.stream()
                     .map(this::toOrderDTOWithProductInfo)
                     .collect(Collectors.toList());
    }
    
    // --- CÁC PHƯƠNG THỨC MAPPER ---

    private OrderDto toOrderDTOWithProductInfo(Order entity) {
        OrderDto dto = toOrderDTO(entity);
        // Ưu tiên tour
        List<BookingTour> tours = bookingTourDAO.findByOrderId(entity.getId());
        if (tours != null && !tours.isEmpty()) {
            dto.setMainProduct(tours.get(0).getDeparture().getTour().getName());
        } else {
            // Nếu không phải tour, kiểm tra hotel
            List<HotelBooking> hotels = hotelBookingDAO.findByOrderId(entity.getId());
            if (hotels != null && !hotels.isEmpty()) {
                dto.setMainProduct(hotels.get(0).getRoomVariant().getRoom().getHotel().getName());
            }
        }
        if (dto.getMainProduct() == null) {
            dto.setMainProduct("Nhiều dịch vụ");
        }
        return dto;
    }

    private OrderDto toOrderDTO(Order entity) {
        OrderDto dto = new OrderDto();
        dto.setId(entity.getId());
        dto.setAmount(entity.getAmount());
        dto.setStatus(entity.getStatus());
        dto.setPayDate(entity.getPayDate());
        dto.setCreatedAt(entity.getCreatedAt());

        if (entity.getExpiresAt() != null) {
            dto.setExpiresAt(entity.getExpiresAt());
        }

        if (entity.getUser() != null) dto.setUserId(entity.getUser().getId());
        if (entity.getVoucher() != null) dto.setVoucherId(entity.getVoucher().getId());
        if (entity.getDestination() != null) dto.setDestinationId(entity.getDestination().getId());
        
        return dto;
    }
    
    // --- CÁC HÀM HELPER KHÁC ---
    // XÓA: private BigDecimal calculateTotalAmount(TicketDetail cart) { ... }
    // XÓA: private boolean isCartEmpty(TicketDetail cart) { ... }
    // XÓA: private boolean processPayment(Order order, CheckoutDto checkoutDto) { ... }
}
