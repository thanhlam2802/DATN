package backend.backend.implement;

import backend.backend.controller.OrderController;
import backend.backend.dao.*;
import backend.backend.dao.Bus.BusSeatDAO;
import backend.backend.dao.Bus.BusSlotDAO;
import backend.backend.dto.*;
import backend.backend.dto.BusDTO.BusBookingDto;
import backend.backend.dto.BusDTO.DirectBusReservationRequestDto;
import backend.backend.entity.*;
import backend.backend.entity.enumBus.BusBookingStatus;
import backend.backend.event.VoucherUsedUpEvent;
import backend.backend.exception.ResourceNotFoundException;
import backend.backend.service.OrderService;
import lombok.RequiredArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);
     private final BusBookingDAO busBookingDAO;
     private final BookingTourDAO bookingTourDAO;
     private final CustomerDAO customerDAO;
     private final DepartureDAO departureDAO;
     private final FlightBookingDAO flightBookingDAO;
     private final VoucherDAO voucherDAO;
     private final FlightSlotDAO flightSlotDAO;
     private final HotelBookingDAO hotelBookingDAO;
     private final OrderDAO orderDAO;
     private final TourDAO tourDAO;
    private final UserDAO userDAO;
    private final BusSlotDAO busSlotDAO;
    private final BusSeatDAO busSeatDAO;
    private final  ApplicationEventPublisher eventPublisher;

    private User mustGetCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            throw new AuthenticationCredentialsNotFoundException("Bạn chưa đăng nhập");
        }
        Object p = auth.getPrincipal();
        if (p instanceof User u) return u;
        // (Trong filter của bạn principal = User rồi, fallback vẫn trả 401)
        throw new AuthenticationCredentialsNotFoundException("Bạn chưa đăng nhập");
    }

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
        bookingTour.setEmail(directRequest.getEmail());
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
        logger.info("fl status sau khi save: {}",SecurityContextHolder.getContext().getAuthentication());

        User user =(User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();


        // 2. Lấy slot và flight
        FlightSlot slot = flightSlotDAO.findById(directRequest.getFlightSlotId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy slot ghế."));
        Flight flight = slot.getFlight();

        // 3. Kiểm tra slot đã được đặt chưa
        boolean slotBooked = slot.getStatus().equalsIgnoreCase("used");
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
        slot.setStatus("USED");
        FlightSlot fl =  flightSlotDAO.save(slot);
        logger.info("fl status sau khi save: {}",fl.getStatus());
        return flightBookingDAO.save(booking).getId();
    }

    @Override
    @Transactional
    public Integer createDirectBusReservation(DirectBusReservationRequestDto directRequest) {
        List<String> selectedSeatNumbers = directRequest.getSelectedSeatNumbers();
        if (selectedSeatNumbers == null || selectedSeatNumbers.isEmpty()) {
            throw new IllegalArgumentException("Phải chọn ít nhất một ghế.");
        }

        User user = mustGetCurrentUser(); // ✅ KHÔNG lấy từ request nữa

        BusSlot busSlot = busSlotDAO.findById(directRequest.getBusSlotId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy chuyến xe."));

        // ✅ LOCK GHẾ theo seat numbers
        List<BusSeat> selectedSeats = busSeatDAO.lockSeatsForUpdate(busSlot.getId(), selectedSeatNumbers);
        if (selectedSeats.size() != selectedSeatNumbers.size()) {
            throw new IllegalStateException("Một số ghế không tồn tại.");
        }

        // ✅ Check ghế đã booked chưa
        List<String> alreadyBooked = selectedSeats.stream()
                .filter(BusSeat::getIsBooked)
                .map(BusSeat::getSeatNumber)
                .collect(Collectors.toList());
        if (!alreadyBooked.isEmpty()) {
            throw new IllegalStateException("Các ghế đã có người đặt: " + String.join(", ", alreadyBooked));
        }

        // ✅ Tính tiền
        BigDecimal totalPrice = selectedSeats.stream()
                .map(BusSeat::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // ✅ Tạo order
        Order order = new Order();
        order.setUser(user);
        order.setAmount(totalPrice);
        order.setStatus("PENDING_PAYMENT");
        order.setExpiresAt(LocalDateTime.now().plusMinutes(30));
        order.setCreatedAt(LocalDateTime.now());
        Order savedOrder = orderDAO.save(order);

        // ✅ Customer
        Customer customer = new Customer();
        customer.setFullName(directRequest.getCustomerName());
        customer.setPhone(directRequest.getPhone());
        customer.setEmail(directRequest.getEmail());
        Customer savedCustomer = customerDAO.save(customer);

        // ✅ Booking
        BusBooking booking = new BusBooking();
        booking.setBusSlot(busSlot);
        booking.setOrder(savedOrder);
        booking.setCustomer(savedCustomer);
        booking.setStatus(BusBookingStatus.RESERVED);
        booking.setNumPassengers(selectedSeats.size());
        booking.setTotalPrice(totalPrice);
        booking.setBookingDate(LocalDateTime.now());
        booking.setSelectedSeats(selectedSeats);
        busBookingDAO.save(booking);

        // ✅ Đánh dấu ghế đã booked (vẫn trong cùng transaction/lock)
        selectedSeats.forEach(s -> s.setIsBooked(true));
        busSeatDAO.saveAll(selectedSeats);

        return savedOrder.getId();
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
        List<BusBooking> busBookings = busBookingDAO.findByOrderId(entity.getId());

        int totalItems = tourBookings.size() + flightBookings.size() + hotelBookings.size() + busBookings.size();

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
            }   else if (!busBookings.isEmpty()) {
                BusBooking bus = busBookings.get(0);
                String busName = "Vé xe khách";
                if (bus.getBusSlot() != null && bus.getBusSlot().getBus() != null) {
                    busName = bus.getBusSlot().getBus().getName();
                }
                String routeInfo = "";
                if (bus.getBusSlot() != null && bus.getBusSlot().getRoute() != null) {
                    routeInfo = " (" + getSimpleLocationName(bus.getBusSlot().getRoute().getOriginLocation()) +
                            " → " + getSimpleLocationName(bus.getBusSlot().getRoute().getDestinationLocation()) + ")";
                }
                mainProductName = busName + routeInfo;
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
        dto.setTransactionId(entity.getTransactionId());
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
        dto.setBusBookings(busBookingDAO.findByOrderIdWithSeats(entity.getId()).stream().map(this::toBusBookingDto).collect(Collectors.toList())); // ✅ ADD


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
        dto.setRooms(hotelBooking.getRooms());
        return dto;
    }

    /**
     * Logic chính để áp dụng voucher vào đơn hàng.
     *
     * @param orderId ID của đơn hàng.
     * @param voucherCode Mã voucher.
     * @return OrderDTO đã được cập nhật.
     */
    @Transactional
    public OrderDto applyVoucherToOrder(Integer orderId, String voucherCode) {
        Order order = orderDAO.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng với ID: " + orderId));

        if (!"PENDING_PAYMENT".equals(order.getStatus())) {
            throw new RuntimeException("Chỉ có thể áp dụng voucher cho đơn hàng đang chờ thanh toán.");
        }

        if (order.getVoucher() != null) {
            throw new RuntimeException("Mỗi đơn hàng chỉ được áp dụng một mã giảm giá.");
        }

        Voucher voucher = voucherDAO.findByCode(voucherCode)
                .orElseThrow(() -> new RuntimeException("Mã giảm giá '" + voucherCode + "' không tồn tại."));

    
        validateVoucher(voucher, order.getAmount());
     
        BigDecimal originalAmount = order.getAmount();
        BigDecimal discount = calculateEffectiveDiscount(voucher, originalAmount);
        BigDecimal newAmount = originalAmount.subtract(discount);
        
        // 7. Cập nhật thông tin đơn hàng
        order.setOriginalAmount(originalAmount); 
        order.setAmount(newAmount);              
        order.setVoucher(voucher);
        voucher.setUsageCount(voucher.getUsageCount() + 1);
        Voucher updatedVoucher = voucherDAO.save(voucher);

        // KIỂM TRA VÀ PHÁT SỰ KIỆN NẾU VOUCHER ĐÃ HẾT
        if (updatedVoucher.getUsageLimit() != null && updatedVoucher.getUsageCount() >= updatedVoucher.getUsageLimit()) {
            eventPublisher.publishEvent(new VoucherUsedUpEvent(this, updatedVoucher.getCode()));
        }

        Order updatedOrder = orderDAO.save(order);

        return convertToDto(updatedOrder); 
    }


    private void validateVoucher(Voucher voucher, BigDecimal orderAmount) {
        if (voucher.getStatus() != VoucherStatus.ACTIVE) {
            throw new RuntimeException("Mã giảm giá này không còn hoạt động.");
        }
        LocalDate today = LocalDate.now();
        if (today.isBefore(voucher.getStartDate()) || today.isAfter(voucher.getExpiryDate())) {
            throw new RuntimeException("Mã giảm giá đã hết hạn hoặc chưa đến ngày sử dụng.");
        }
        if (voucher.getUsageLimit() != null && voucher.getUsageCount() >= voucher.getUsageLimit()) {
            throw new RuntimeException("Mã giảm giá đã hết lượt sử dụng.");
        }
        if (voucher.getConditionMinAmount() != null && orderAmount.compareTo(voucher.getConditionMinAmount()) < 0) {
            throw new RuntimeException("Đơn hàng chưa đủ điều kiện tối thiểu để áp dụng mã này.");
        }
    }
    /**
     * Hàm helper để tính toán số tiền giảm giá thực tế của một voucher.
     * VỊ TRÍ ĐÚNG CỦA NÓ LÀ Ở ĐÂY.
     */
    private BigDecimal calculateEffectiveDiscount(Voucher voucher, BigDecimal orderAmount) {
        if (voucher.getType() == VoucherType.FIXED_AMOUNT) {
            return voucher.getDiscountAmount();
        }
        if (voucher.getType() == VoucherType.PERCENTAGE) {
            BigDecimal discount = orderAmount.multiply(BigDecimal.valueOf(voucher.getDiscountPercentage() / 100.0));
            if (voucher.getMaxDiscountAmount() != null && discount.compareTo(voucher.getMaxDiscountAmount()) > 0) {
                return voucher.getMaxDiscountAmount();
            }
            return discount;
        }
        return BigDecimal.ZERO;
    }

    // ✅ ADD MISSING getSimpleLocationName METHOD
    private String getSimpleLocationName(Location location) {
        if (location == null) return "N/A";

        StringBuilder display = new StringBuilder();

        if (location.getName() != null && !location.getName().trim().isEmpty()) {
            display.append(location.getName());
        }

        if (location.getProvinceCity() != null && !location.getProvinceCity().trim().isEmpty()) {
            if (display.length() > 0) display.append(" - ");
            display.append(location.getProvinceCity());
        }

        if (location.getDistrict() != null && !location.getDistrict().trim().isEmpty()) {
            if (display.length() > 0) display.append(" - ");
            display.append(location.getDistrict());
        }

        return display.length() > 0 ? display.toString() : "N/A";
    }

    // ✅ FIX BROKEN convertToDto METHOD

    private OrderDto convertToDto(Order order) {
        if (order == null) return null;
        // This method combines the logic of toOrderDTO and toDetailedOrderDto,
        // and also maps the 'originalAmount' which is crucial after applying a voucher.
        OrderDto dto = new OrderDto();
        dto.setId(order.getId());
        dto.setAmount(order.getAmount());
        dto.setOriginalAmount(order.getOriginalAmount());
        dto.setStatus(order.getStatus());
        dto.setPayDate(order.getPayDate());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setExpiresAt(order.getExpiresAt());

        if (order.getUser() != null) {
            dto.setUserId(order.getUser().getId());
        }
        if (order.getVoucher() != null) {
            dto.setVoucherId(order.getVoucher().getId());
        }
        if (order.getDestination() != null) {
            dto.setDestinationId(order.getDestination().getId());
        }
        dto.setTourBookings(bookingTourDAO.findByOrderId(order.getId()).stream().map(this::toBookingTourDto).collect(Collectors.toList()));
        dto.setFlightBookings(flightBookingDAO.findByOrderId(order.getId()).stream().map(this::toFlightBookingDto).collect(Collectors.toList()));
        dto.setHotelBookings(hotelBookingDAO.findByOrderId(order.getId()).stream().map(this::toHotelBookingDto).collect(Collectors.toList()));
        dto.setBusBookings(busBookingDAO.findByOrderId(order.getId()).stream().map(this::toBusBookingDto).collect(Collectors.toList()));
        return dto;
    }

    @Override
    @Transactional
    public OrderDto cancelOrderAfterRefund(Integer orderId) {
        logger.info("Bắt đầu hủy đơn hàng sau hoàn tiền cho Order ID: {}", orderId);
        Order order = orderDAO.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng với ID: " + orderId));
        if (!"PAID".equals(order.getStatus())) {
            throw new RuntimeException("Chỉ có thể hủy đơn hàng đã thanh toán.");
        }
        // Cập nhật trạng thái đơn hàng thành CANCELLED
        order.setStatus("CANCELLED");
        orderDAO.save(order);
        // Xử lý các booking trong đơn hàng
        // 1. Flight bookings - cập nhật flight slots thành AVAILABLE
        if (order.getFlightBookings() != null && !order.getFlightBookings().isEmpty()) {
            for (FlightBooking flightBooking : order.getFlightBookings()) {
                FlightSlot flightSlot = flightBooking.getFlightSlot();
                if (flightSlot != null) {
                    flightSlot.setStatus("AVAILABLE");
                    flightSlotDAO.save(flightSlot);
                    logger.info("Đã cập nhật flight slot {} thành AVAILABLE", flightSlot.getId());
                }
            }
        }
        // 2. Hotel bookings - placeholder logic
        if (order.getHotelBookings() != null && !order.getHotelBookings().isEmpty()) {
            logger.info("Đơn hàng có {} hotel bookings - xử lý placeholder", order.getHotelBookings().size());
            // TODO: Implement hotel booking cancellation logic
        }
        // 3. Tour bookings - placeholder logic
        if (order.getBookingTours() != null && !order.getBookingTours().isEmpty()) {
            logger.info("Đơn hàng có {} tour bookings - xử lý placeholder", order.getBookingTours().size());
            // TODO: Implement tour booking cancellation logic
        }
        // 4. Bus bookings - placeholder logic
        if (order.getBusBookings() != null && !order.getBusBookings().isEmpty()) {
            logger.info("Đơn hàng có {} bus bookings - xử lý placeholder", order.getBusBookings().size());
            // TODO: Implement bus booking cancellation logic
        }

        logger.info("Hủy đơn hàng thành công cho Order ID: {}", orderId);
        return toOrderDTO(order);
    }

    @Transactional(readOnly = true)
    protected BusBookingDto toBusBookingDto(BusBooking busBooking) {
        BusBookingDto dto = new BusBookingDto();

        // Basic info
        dto.setId(busBooking.getId());
        dto.setBookingReference(busBooking.getBookingReference());
        dto.setStatus(busBooking.getStatus().name()); // Convert enum to string
        dto.setNumPassengers(busBooking.getNumPassengers());
        dto.setTotalPrice(busBooking.getTotalPrice());
        dto.setBookingDate(busBooking.getBookingDate());
        dto.setOrderId(busBooking.getOrder() != null ? busBooking.getOrder().getId() : null);

        // Customer info
        if (busBooking.getCustomer() != null) {
            dto.setCustomerId(busBooking.getCustomer().getId());
            dto.setCustomerName(busBooking.getCustomer().getFullName());
            dto.setCustomerPhone(busBooking.getCustomer().getPhone());
            dto.setCustomerEmail(busBooking.getCustomer().getEmail());
        }

        // Bus slot info
        if (busBooking.getBusSlot() != null) {
            dto.setBusSlotId(busBooking.getBusSlot().getId());
            dto.setDepartureDate(busBooking.getBusSlot().getSlotDate());
            dto.setDepartureTime(busBooking.getBusSlot().getDepartureTime());
            dto.setArrivalTime(busBooking.getBusSlot().getArrivalTime());

            // ✅ ADD: Map route info (avoid lazy loading)
            if (busBooking.getBusSlot().getRoute() != null) {
                dto.setDepartureLocation(busBooking.getBusSlot().getRoute().getOriginLocation().getName());
                dto.setArrivalLocation(busBooking.getBusSlot().getRoute().getDestinationLocation().getName());
            }

            if (busBooking.getBusSlot().getBus() != null) {
                dto.setBusName(busBooking.getBusSlot().getBus().getName());
                dto.setBusLicensePlate(busBooking.getBusSlot().getBus().getLicensePlate());

                // ✅ ADD: Map bus category name only (avoid lazy loading)
                if (busBooking.getBusSlot().getBus().getCategory() != null) {
                    dto.setBusCategoryName(busBooking.getBusSlot().getBus().getCategory().getName());
                }
            }
        }

        // ✅ ENHANCED: Map selected seats (avoid lazy loading)
        if (busBooking.getSelectedSeats() != null) {
            // Map seat numbers as strings
            List<String> seatNumbers = busBooking.getSelectedSeats().stream()
                    .map(seat -> seat.getSeatNumber())
                    .collect(Collectors.toList());
            dto.setSeatNumbers(seatNumbers);
        }

        return dto;
    }
}