package backend.backend.implement;

import backend.backend.dao.*;
import backend.backend.dto.CheckoutDto;
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
    @Autowired private TicketDetailDAO ticketDetailDAO;
    @Autowired private VoucherDAO voucherDAO;
    @Autowired private UserDAO userDAO;
    @Autowired private DepartureDAO departureDAO;
    @Autowired private BookingTourDAO bookingTourDAO;
    @Autowired private TourDAO tourDAO;

    @Override
    @Transactional
    public OrderDto placeOrder(CheckoutDto checkoutDto) {
        TicketDetail cart = ticketDetailDAO.findById(checkoutDto.getTicketDetailId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giỏ hàng với ID: " + checkoutDto.getTicketDetailId()));

        if (!"CART".equalsIgnoreCase(cart.getStatus())) {
            throw new IllegalStateException("Giỏ hàng này đã được xử lý hoặc không hợp lệ.");
        }

        if (isCartEmpty(cart)) {
            throw new IllegalStateException("Giỏ hàng rỗng, không thể đặt hàng.");
        }

        BigDecimal totalAmount = calculateTotalAmount(cart);

        Voucher voucher = null;
        if (checkoutDto.getVoucherId() != null) {
            voucher = voucherDAO.findById(checkoutDto.getVoucherId())
                    .orElseThrow(() -> new ResourceNotFoundException("Voucher không hợp lệ."));
        }

        Order order = new Order();
        order.setUser(cart.getUser());
        order.setTicketDetail(cart);
        order.setAmount(totalAmount);
        order.setStatus("PENDING");
        order.setVoucher(voucher);

        Order savedOrder = orderDAO.save(order);
        boolean paymentSuccess = processPayment(savedOrder, checkoutDto);

        if (paymentSuccess) {
            savedOrder.setStatus("PAID");
            savedOrder.setPayDate(LocalDateTime.now());
            cart.setStatus("CONFIRMED");
        } else {
            savedOrder.setStatus("FAILED");
        }
        
        orderDAO.save(savedOrder);
        ticketDetailDAO.save(cart);
        
        // SỬA LỖI: Gọi đúng mapper
        return toOrderDTO(savedOrder);
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

        TicketDetail tempTicketDetail = new TicketDetail();
        tempTicketDetail.setUser(user);
        tempTicketDetail.setStatus("PENDING_PAYMENT");

        BookingTour bookingTour = new BookingTour();
        bookingTour.setDeparture(departure);
        bookingTour.setTicketDetail(tempTicketDetail);
        bookingTour.setCustomerName(directRequest.getCustomerName());
        bookingTour.setPhone(directRequest.getPhone());
        bookingTour.setNumberOfAdults(directRequest.getNumberOfAdults());
        bookingTour.setNumberOfChildren(directRequest.getNumberOfChildren());
        bookingTour.setNotes(directRequest.getNotes());
        bookingTour.setTotalPrice(totalPrice);
        bookingTour.setBookingDate(LocalDate.now());
        
        tempTicketDetail.setTourBookings(Collections.singletonList(bookingTour));

        Order temporaryOrder = new Order();
        temporaryOrder.setUser(user);
        temporaryOrder.setTicketDetail(tempTicketDetail);
        temporaryOrder.setAmount(totalPrice);
        temporaryOrder.setStatus("PENDING_PAYMENT");
        temporaryOrder.setExpiresAt(LocalDateTime.now().plusMinutes(30));

        Order savedOrder = orderDAO.save(temporaryOrder);

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
        if (entity.getTicketDetail() != null) {
            if (entity.getTicketDetail().getTourBookings() != null && !entity.getTicketDetail().getTourBookings().isEmpty()) {
                dto.setMainProduct(entity.getTicketDetail().getTourBookings().get(0).getDeparture().getTour().getName());
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
        if (entity.getTicketDetail() != null) dto.setTicketDetailId(entity.getTicketDetail().getId());
        
        return dto;
    }
    
    // --- CÁC HÀM HELPER KHÁC ---
    private BigDecimal calculateTotalAmount(TicketDetail cart) {
        BigDecimal total = BigDecimal.ZERO;
        if (cart.getTourBookings() != null) {
            total = total.add(cart.getTourBookings().stream()
                    .map(tb -> tb.getTotalPrice())
                    .reduce(BigDecimal.ZERO, BigDecimal::add));
        }
        // Thêm logic cho các loại booking khác nếu cần
        return total;
    }
    
    private boolean isCartEmpty(TicketDetail cart) {
        return (cart.getTourBookings() == null || cart.getTourBookings().isEmpty());
        // Thêm logic cho các loại booking khác nếu cần
    }
    
    private boolean processPayment(Order order, CheckoutDto checkoutDto) {
        System.out.println("Processing payment for order #" + order.getId() + " with amount " + order.getAmount());
        return true;
    }
}
