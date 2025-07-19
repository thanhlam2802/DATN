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
    public OrderDto createDirectFlightReservation(DirectFlightReservationRequestDto directRequest) {
        return null;
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
        // Lấy tên sản phẩm chính từ các booking liên kết với order
        List<BookingTour> tours = bookingTourDAO.findByOrderId(entity.getId());
        if (tours != null && !tours.isEmpty()) {
            dto.setMainProduct(tours.get(0).getDeparture().getTour().getName());
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
