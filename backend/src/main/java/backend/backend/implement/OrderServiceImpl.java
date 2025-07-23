package backend.backend.implement;

import backend.backend.dao.*;
import backend.backend.dto.BookingTourDto;
import backend.backend.dto.CheckoutDto;
import backend.backend.dto.DirectFlightReservationRequestDto;
import backend.backend.dto.DirectTourReservationRequestDto;
import backend.backend.dto.FlightBookingDto;
import backend.backend.dto.OrderDto;
import backend.backend.entity.*;
import backend.backend.exception.ResourceNotFoundException;
import backend.backend.service.OrderService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderServiceImpl implements OrderService {

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
    Order order =
        orderDAO
            .findById(checkoutDto.getOrderId())
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        "Không tìm thấy đơn hàng với ID: " + checkoutDto.getOrderId()));

    if (!"CART".equalsIgnoreCase(order.getStatus())
        && !"PENDING_PAYMENT".equalsIgnoreCase(order.getStatus())) {
      throw new IllegalStateException("Đơn hàng này đã được xử lý hoặc không hợp lệ.");
    }

    if (checkoutDto.getVoucherId() != null) {
      Voucher voucher =
          voucherDAO
              .findById(checkoutDto.getVoucherId())
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
    Order order =
        orderDAO
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hóa đơn với ID: " + id));
    
    return toDetailedOrderDto(order);
  }

  @Override
  @Transactional
  public OrderDto createDirectTourReservation(DirectTourReservationRequestDto directRequest) {
    User user =
        userDAO
            .findById(directRequest.getUserId())
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        "Không tìm thấy người dùng với ID: " + directRequest.getUserId()));

    Departure departure =
        departureDAO
            .findById(directRequest.getDepartureId())
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        "Không tìm thấy ngày khởi hành với ID: " + directRequest.getDepartureId()));

    // 1. Tính tổng số ghế cần đặt
    int seatsToBook = directRequest.getNumberOfAdults() + directRequest.getNumberOfChildren();
    
    // 2. Kiểm tra số chỗ còn trống
    int availableSeats = departure.getSeatCount() - departure.getBookedSeats();
    if (seatsToBook > availableSeats) {
        throw new IllegalStateException(
            "Không đủ chỗ trống cho chuyến đi này. Chỉ còn lại " + availableSeats + " chỗ.");
    }

    // 3. Cập nhật số chỗ đã đặt cho chuyến đi
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
    return orders.stream().map(this::toOrderDTOWithProductInfo).collect(Collectors.toList());
  }

  // --- Mapper Methods ---

  private OrderDto toOrderDTOWithProductInfo(Order entity) {
    OrderDto dto = toOrderDTO(entity);
    String mainProductName = null;

    List<BookingTour> tourBookings = bookingTourDAO.findByOrderId(entity.getId());
    List<FlightBooking> flightBookings = flightBookingDAO.findByOrderId(entity.getId());
    
    int totalItems = tourBookings.size() + flightBookings.size() ;

    if (totalItems == 1) {
      if (!tourBookings.isEmpty()) {
        mainProductName = tourBookings.get(0).getDeparture().getTour().getName();
      } else if (!flightBookings.isEmpty()) {
        mainProductName = flightBookings.get(0).getFlightSlot().getFlight().getName();
      } 
    }

    if (mainProductName != null) {
      dto.setMainProduct(mainProductName);
    } else {
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

  private OrderDto toDetailedOrderDto(Order entity) {
    OrderDto dto = toOrderDTO(entity); 

    List<BookingTour> tourBookings = bookingTourDAO.findByOrderId(entity.getId());
    dto.setTourBookings(
        tourBookings.stream().map(this::toBookingTourDto).collect(Collectors.toList()));

    List<FlightBooking> flightBookings = flightBookingDAO.findByOrderId(entity.getId());
    dto.setFlightBookings(
        flightBookings.stream().map(this::toFlightBookingDto).collect(Collectors.toList()));
    
    return dto;
  }

  private BookingTourDto toBookingTourDto(BookingTour tourBooking) {
	    // Lấy các đối tượng liên quan
	    Departure departure = tourBooking.getDeparture();
	    Tour tour = departure.getTour();
	    Order order = tourBooking.getOrder();

	    // Sử dụng @Builder từ DTO để tạo đối tượng
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
	            .orderId(order != null ? order.getId() : null)
	            .build();
	}

private FlightBookingDto toFlightBookingDto(FlightBooking flightBooking) {
   // Lấy các đối tượng liên quan để truy cập ID
   FlightSlot slot = flightBooking.getFlightSlot();
   Flight flight = slot.getFlight();
   Order order = flightBooking.getOrder();
   Customer customer = flightBooking.getCustomer();

   // Tạo DTO và điền thông tin theo cấu trúc mới
   FlightBookingDto dto = new FlightBookingDto();
   
   // Thông tin từ chính FlightBooking
   dto.setId(flightBooking.getId());
   dto.setBookingDate(flightBooking.getBookingDate());
   dto.setTotalPrice(flightBooking.getTotalPrice().doubleValue());

   // Lấy ID từ các đối tượng liên quan
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

   // Xử lý seatCodes
   // Giả định mỗi booking hiện tại chỉ có 1 ghế, tạo list chứa 1 ghế đó
   if (slot.getSeatNumber() != null && !slot.getSeatNumber().isEmpty()) {
       dto.setSeatCodes(java.util.Collections.singletonList(slot.getSeatNumber()));
   }

   return dto;
}

}