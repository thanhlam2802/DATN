package backend.backend.implement;

import backend.backend.controller.OrderController;
import backend.backend.dao.*;
import backend.backend.dto.*;
import backend.backend.entity.*;
import backend.backend.exception.ResourceNotFoundException;
import backend.backend.service.OrderService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderServiceImpl implements OrderService {
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);
    @Autowired private BusBookingDAO busBookingDAO;
    @Autowired private BookingTourDAO bookingTourDAO;
    @Autowired private CustomerDAO customerDAO;
    @Autowired private DepartureDAO departureDAO;
    @Autowired private FlightBookingDAO flightBookingDAO;
    @Autowired private FlightSlotDAO flightSlotDAO;
    @Autowired private HotelBookingDAO hotelBookingDAO;
    @Autowired private OrderDAO orderDAO;
    @Autowired private TourDAO tourDAO;
    @Autowired private UserDAO userDAO;
    @Autowired private VoucherDAO voucherDAO;

    @Override
    @Transactional
    public OrderDto placeOrder(CheckoutDto checkoutDto) {
        Order order = orderDAO.findById(checkoutDto.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng với ID: " + checkoutDto.getOrderId()));

        if (!"CART".equalsIgnoreCase(order.getStatus()) && !"PENDING_PAYMENT".equalsIgnoreCase(order.getStatus())) {
            throw new IllegalStateException("Đơn hàng này đã được xử lý hoặc không hợp lệ.");
        }

        if (checkoutDto.getVoucherId() != null) {
            Voucher voucher = voucherDAO.findById(checkoutDto.getVoucherId())
                    .orElseThrow(() -> new ResourceNotFoundException("Voucher không hợp lệ."));
            order.setVoucher(voucher);
        }

        order.setStatus("PAID");
        order.setPayDate(LocalDateTime.now());
        orderDAO.save(order);

        return toOrderDTO(order);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDto getOrderById(Integer id) {
        Order order = orderDAO.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hóa đơn với ID: " + id));
        return toDetailedOrderDto(order);
    }

    @Override
    @Transactional
    public OrderDto createDirectTourReservation(DirectTourReservationRequestDto directRequest) {
        User user = userDAO.findById(directRequest.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với ID: " + directRequest.getUserId()));

        Departure departure = departureDAO.findById(directRequest.getDepartureId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy ngày khởi hành với ID: " + directRequest.getDepartureId()));

        int seatsToBook = directRequest.getNumberOfAdults() + directRequest.getNumberOfChildren();
        int availableSeats = departure.getSeatCount() - departure.getBookedSeats();
        if (seatsToBook > availableSeats) {
            throw new IllegalStateException("Không đủ chỗ trống cho chuyến đi này. Chỉ còn lại " + availableSeats + " chỗ.");
        }
        
        departure.setBookedSeats(departure.getBookedSeats() + seatsToBook);

        BigDecimal adultPrice = departure.getAdultPrice();
        BigDecimal childPrice = departure.getChildPrice();
        BigDecimal totalAdultPrice = adultPrice.multiply(new BigDecimal(directRequest.getNumberOfAdults()));
        BigDecimal totalChildPrice = childPrice.multiply(new BigDecimal(directRequest.getNumberOfChildren()));
        BigDecimal totalPrice = totalAdultPrice.add(totalChildPrice);

        Order temporaryOrder = new Order();
        temporaryOrder.setUser(user);
        temporaryOrder.setAmount(totalPrice);
        temporaryOrder.setStatus("PENDING_PAYMENT");
        temporaryOrder.setExpiresAt(LocalDateTime.now().plusMinutes(30));
        temporaryOrder.setCreatedAt(LocalDateTime.now());
        Order savedOrder = orderDAO.save(temporaryOrder);

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
    public Integer createDirectFlightReservation(DirectFlightReservationRequestDto directRequest) {
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

        return flightBookingDAO.save(booking).getId();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDto> getOrdersByUserId(Integer userId) {
        List<Order> orders = orderDAO.findByUserIdOrderByCreatedAtDesc(userId);
        return orders.stream().map(this::toOrderDTOWithProductInfo).collect(Collectors.toList());
    }

    @Override
    public OrderDto paidOrder(Integer id, String transactionId) {
        Order order = orderDAO.findById(id).orElse(null);
        logger.info("order : {} được ghi nhận.", id);
        if (order != null) {
            order.setTransactionId(transactionId);
            order.setStatus("PAID");
            order.setPayDate(LocalDateTime.now());
            orderDAO.save(order);
            logger.info("––– Order Saved –––");
        }
        return toOrderDTO(order);
    }
    // --- Mapper Methods ---

    private OrderDto toOrderDTOWithProductInfo(Order entity) {
        OrderDto dto = toOrderDTO(entity);
        String mainProductName = null;

        List<BookingTour> tourBookings = bookingTourDAO.findByOrderId(entity.getId());
        List<FlightBooking> flightBookings = flightBookingDAO.findByOrderId(entity.getId());
        List<HotelBooking> hotelBookings = hotelBookingDAO.findByOrderId(entity.getId());

        int totalItems = tourBookings.size() + flightBookings.size() + hotelBookings.size();

        if (totalItems == 1) {
            if (!tourBookings.isEmpty()) {
                mainProductName = tourBookings.get(0).getDeparture().getTour().getName();
            } else if (!flightBookings.isEmpty()) {
                mainProductName = flightBookings.get(0).getFlightSlot().getFlight().getName();
            } else if (!hotelBookings.isEmpty()) {
                HotelBooking hotel = hotelBookings.get(0);
                String hotelName = "Khách sạn";
                if (hotel.getRoomVariant() != null && hotel.getRoomVariant().getRoom() != null && hotel.getRoomVariant().getRoom().getHotel() != null) {
                    hotelName = hotel.getRoomVariant().getRoom().getHotel().getName();
                }
                String variantName = hotel.getRoomVariant() != null ? hotel.getRoomVariant().getVariantName() : "";
                mainProductName = hotelName + (variantName.isEmpty() ? "" : (" - " + variantName));
            }
        }

        dto.setMainProduct(totalItems > 1 ? "Nhiều dịch vụ" : mainProductName);
        return dto;
    }

    private OrderDto toOrderDTO(Order entity) {
        if (entity == null) return null;
        OrderDto dto = new OrderDto();
        dto.setId(entity.getId());
        dto.setAmount(entity.getAmount());
        dto.setStatus(entity.getStatus());
        dto.setPayDate(entity.getPayDate());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setExpiresAt(entity.getExpiresAt());
        if (entity.getUser() != null) dto.setUserId(entity.getUser().getId());
        if (entity.getVoucher() != null) dto.setVoucherId(entity.getVoucher().getId());
        if (entity.getDestination() != null) dto.setDestinationId(entity.getDestination().getId());
        return dto;
    }

    private OrderDto toDetailedOrderDto(Order entity) {
        OrderDto dto = toOrderDTO(entity);
        dto.setTourBookings(bookingTourDAO.findByOrderId(entity.getId()).stream().map(this::toBookingTourDto).collect(Collectors.toList()));
        dto.setFlightBookings(flightBookingDAO.findByOrderId(entity.getId()).stream().map(this::toFlightBookingDto).collect(Collectors.toList()));
        dto.setHotelBookings(hotelBookingDAO.findByOrderId(entity.getId()).stream().map(this::toHotelBookingDto).collect(Collectors.toList()));
        return dto;
    }

    private BookingTourDto toBookingTourDto(BookingTour tourBooking) {
        Departure departure = tourBooking.getDeparture();
        Tour tour = departure.getTour();
        return BookingTourDto.builder()
                .id(tourBooking.getId())
                .tourId(tour.getId())
                .tourName(tour.getName())
                .departureDate(departure.getDepartureDate())
                .numberOfAdults(tourBooking.getNumberOfAdults())
                .numberOfChildren(tourBooking.getNumberOfChildren())
                .adultPrice(departure.getAdultPrice())
                .childPrice(departure.getChildPrice())
                .totalPrice(tourBooking.getTotalPrice())
                .orderId(tourBooking.getOrder() != null ? tourBooking.getOrder().getId() : null)
                .build();
    }

    private FlightBookingDto toFlightBookingDto(FlightBooking flightBooking) {
        FlightSlot slot = flightBooking.getFlightSlot();
        Flight flight = slot.getFlight();
        Order order = flightBooking.getOrder();
        Customer customer = flightBooking.getCustomer();

        FlightBookingDto dto = new FlightBookingDto();
        dto.setId(flightBooking.getId());
        dto.setBookingDate(flightBooking.getBookingDate());
        dto.setTotalPrice(flightBooking.getTotalPrice().doubleValue());
        dto.setFlightSlotId(slot.getId());
        dto.setFlightId(flight.getId());
        if (order != null) {
            dto.setOrderId(order.getId());
            if (order.getUser() != null) {
                dto.setUserId(order.getUser().getId());
            }
        }
        if (customer != null) {
            dto.setCustomerId(customer.getId());
        }
        if (slot.getSeatNumber() != null && !slot.getSeatNumber().isEmpty()) {
            dto.setSeatCodes(Collections.singletonList(slot.getSeatNumber()));
        }
        return dto;
    }

    private backend.backend.dto.HotelBookingDto toHotelBookingDto(HotelBooking hotelBooking) {
        backend.backend.dto.HotelBookingDto dto = new backend.backend.dto.HotelBookingDto();
        dto.setId(hotelBooking.getId());
        dto.setRoomVariantId(hotelBooking.getRoomVariant().getId());
        dto.setCheckInDate(hotelBooking.getCheckInDate());
        dto.setCheckOutDate(hotelBooking.getCheckOutDate());
        dto.setNumAdults(hotelBooking.getNumAdults());
        dto.setNumChildren(hotelBooking.getNumChildren());
        dto.setTotalPrice(hotelBooking.getTotalPrice());
        dto.setCreatedAt(hotelBooking.getCreatedAt());
        dto.setOrderId(hotelBooking.getOrder() != null ? hotelBooking.getOrder().getId() : null);
        if (hotelBooking.getOrder() != null && hotelBooking.getOrder().getUser() != null) {
            dto.setUserId(hotelBooking.getOrder().getUser().getId());
        }
        if (hotelBooking.getRoomVariant() != null) {
            dto.setVariantName(hotelBooking.getRoomVariant().getVariantName());
            if (hotelBooking.getRoomVariant().getRoom() != null) {
                dto.setRoomType(hotelBooking.getRoomVariant().getRoom().getRoomType());
                if (hotelBooking.getRoomVariant().getRoom().getHotel() != null) {
                    dto.setHotelName(hotelBooking.getRoomVariant().getRoom().getHotel().getName());
                }
                if (hotelBooking.getRoomVariant().getRoom().getRoomImages() != null && !hotelBooking.getRoomVariant().getRoom().getRoomImages().isEmpty()) {
                    var roomImages = hotelBooking.getRoomVariant().getRoom().getRoomImages();
                    var img = roomImages.get(0);
                    if (img.getImage() != null) {
                        dto.setImageUrl(img.getImage().getUrl());
                    }
                    java.util.List<String> urls = new java.util.ArrayList<>();
                    for (var ri : roomImages) {
                        if (ri.getImage() != null && ri.getImage().getUrl() != null) {
                            urls.add(ri.getImage().getUrl());
                        }
                    }
                    dto.setImageUrls(urls);
                }
            }
        }
        return dto;
    }
}