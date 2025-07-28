package backend.backend.service.implement;

import backend.backend.dao.*;


import backend.backend.dto.*;
import backend.backend.entity.*;
import backend.backend.exception.ResourceNotFoundException; // BỔ SUNG
import backend.backend.service.FlightBookingService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.math.BigDecimal; // BỔ SUNG
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FlightBookingServiceImpl implements FlightBookingService {


    @Autowired
    private FlightBookingDAO flightBookingDAO;
    @Autowired
    private FlightDAO flightDAO;
    @Autowired
    private FlightSlotDAO flightSlotDAO;
    @Autowired
    private CustomerDAO customerDAO;
    @Autowired
    private OrderDAO orderDAO;


    /**
     * SỬA ĐỔI: Phương thức này giờ sẽ cập nhật tổng tiền của Order.
     */
    @Override
    @Transactional // BỔ SUNG
    public FlightBookingDetailDto bookFlight(FlightBookingDto bookingDto) {
        String requestId = UUID.randomUUID().toString();
        log.info("BOOK_FLIGHT_REQUEST      - RequestId: {}, payload: {}", requestId, bookingDto);
        try {
            // 1. Lấy slot
            FlightSlot slot = flightSlotDAO.findById(bookingDto.getFlightSlotId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy slot chuyến bay với ID: " + bookingDto.getFlightSlotId()));

            // 2. Lấy Order (giỏ hàng)
            Order order;
            if (bookingDto.getOrderId() != null) {
                order = orderDAO.findById(bookingDto.getOrderId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy order với ID: " + bookingDto.getOrderId()));
            } else {
                throw new IllegalArgumentException("OrderId là bắt buộc để thêm chuyến bay vào giỏ hàng.");
            }

            // 3. Tính toán giá và cập nhật tổng tiền của Order
            BigDecimal newBookingPrice = slot.getPrice();
           System.out.print(newBookingPrice);
            BigDecimal currentOrderAmount = order.getAmount() != null ? order.getAmount() : BigDecimal.ZERO;
            order.setAmount(currentOrderAmount.add(newBookingPrice));
            orderDAO.save(order);
            

            // 4. Tạo booking flight
            FlightBooking booking = new FlightBooking();
            booking.setFlightSlot(slot);
            booking.setBookingDate(LocalDateTime.now());
            booking.setOrder(order); // Gán đối tượng Order đã được quản lý
            booking.setTotalPrice(newBookingPrice); 
            if (bookingDto.getCustomerId() != null) {
                Customer c = new Customer(); c.setId(bookingDto.getCustomerId());
                booking.setCustomer(c);
            }
            
            flightBookingDAO.save(booking);
            log.info("BOOK_FLIGHT_SUCCESS      - RequestId: {}, bookingId: {}", requestId, booking.getId());

            return toBookingDetailDto(booking, slot.getFlight());
        } catch (Exception e) {
            log.error("BOOK_FLIGHT_FAILED       - RequestId: {}, error: {}", requestId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public PaymentStatusDto payForFlight(PaymentRequestDto paymentRequestDto) {
        String requestId = UUID.randomUUID().toString();
        log.info("PAY_FLIGHT_REQUEST       - RequestId: {}, bookingId: {}", requestId, paymentRequestDto.getBookingId());
        try {
            FlightBooking booking = flightBookingDAO.findById(paymentRequestDto.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đặt vé máy bay với ID: " + paymentRequestDto.getBookingId()));
            
            PaymentStatusDto status = new PaymentStatusDto();
            status.setBookingId(booking.getId());
            status.setStatus("PAID");
            status.setMessage("Thanh toán thành công");
            log.info("PAY_FLIGHT_SUCCESS       - RequestId: {}, bookingId: {}, status: {}", requestId, booking.getId(), status.getStatus());
            return status;
        } catch (Exception e) {
            log.error("PAY_FLIGHT_FAILED        - RequestId: {}, bookingId: {}, error: {}", requestId, paymentRequestDto.getBookingId(), e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<FlightBookingDetailDto> getCustomerFlightBookings(Integer customerId) {
        String requestId = UUID.randomUUID().toString();
        log.info("GET_BOOKINGS_REQUEST     - RequestId: {}, customerId: {}", requestId, customerId);
        try {
            List<FlightBooking> bookings = flightBookingDAO.findByCustomerId(customerId);
            List<FlightBookingDetailDto> dtos = bookings.stream()
                    .map(b -> toBookingDetailDto(b, b.getFlightSlot().getFlight()))
                    .collect(Collectors.toList());
            log.info("GET_BOOKINGS_SUCCESS     - RequestId: {}, customerId: {}, count: {}", requestId, customerId, dtos.size());
            return dtos;
        } catch (Exception e) {
            log.error("GET_BOOKINGS_FAILED      - RequestId: {}, customerId: {}, error: {}", requestId, customerId, e.getMessage(), e);
            throw e;
        }
    }
    @Transactional
    @Override
    public FlightBookingDetailDto getFlightBookingDetail(Integer bookingId) {
        String requestId = UUID.randomUUID().toString();
        log.info("GET_BOOKING_DETAIL_REQ   - RequestId: {}, bookingId: {}", requestId, bookingId);
        try {
            FlightBooking booking = flightBookingDAO.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đặt vé máy bay với ID: " + bookingId));
            
            FlightBookingDetailDto dto = toBookingDetailDto(booking, booking.getFlightSlot().getFlight());
            log.info("GET_BOOKING_DETAIL_SUCCESS - RequestId: {}, bookingId: {}", requestId, bookingId);
            return dto;
        } catch (Exception e) {
            log.error("GET_BOOKING_DETAIL_FAILED  - RequestId: {}, bookingId: {}, error: {}", requestId, bookingId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public PaymentStatusDto cancelFlightBooking(Integer bookingId) {
        String requestId = UUID.randomUUID().toString();
        log.info("CANCEL_BOOKING_REQUEST   - RequestId: {}, bookingId: {}", requestId, bookingId);
        try {
            FlightBooking booking = flightBookingDAO.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đặt vé máy bay với ID: " + bookingId));

            PaymentStatusDto status = new PaymentStatusDto();
            status.setBookingId(booking.getId());
            status.setStatus("CANCELLED");
            status.setMessage("Đã hủy vé thành công");
            log.info("CANCEL_BOOKING_SUCCESS   - RequestId: {}, bookingId: {}, status: {}", requestId, bookingId, status.getStatus());
            return status;
        } catch (Exception e) {
            log.error("CANCEL_BOOKING_FAILED    - RequestId: {}, bookingId: {}, error: {}", requestId, bookingId, e.getMessage(), e);
            throw e;
        }
    }
    @Transactional
    @Override
    public FlightOrderReservationDto getFlightReservationSummary(Integer bookingId) {
        FlightBooking booking = flightBookingDAO.findById(bookingId).orElse(null);
        if (booking == null) return null;
        FlightOrderReservationDto dto = new FlightOrderReservationDto();
        dto.setOrder(toOrderDto(booking.getOrder()));
        dto.setBooking(getFlightBookingDetail(booking.getId()));
        dto.setCustomer(toCustomerDto(booking.getCustomer()));
        dto.setFlightSlot(toFlightSlotDto(booking.getFlightSlot()));
        return dto;
    }

    @Override
    public FlightBooking createFlightBooking(Integer orderId,AddItemRequestDto genericRequest) {
        Customer customer = customerDAO.findById(genericRequest.getCustomerId()).orElse(null);
        FlightSlot flightSlot = flightSlotDAO.findById(genericRequest.getFlightSlotId()).orElse(null);
        FlightBooking booking = new FlightBooking();
        booking.setCustomer(customer);
        booking.setFlightSlot(flightSlot);
        booking.setTotalPrice(flightSlot.getPrice());
        booking.setOrder(orderDAO.findById(orderId).orElse(null));
        booking.setBookingDate(LocalDateTime.now());

        return flightBookingDAO.save(booking);
    }

    @Override
    public void cancelBooking(Integer bookingId) {
        log.info("bookingid: 22572 {}", bookingId);
        FlightBooking fb =  flightBookingDAO.findById(bookingId).orElse(null);
        log.info("info: {}", fb.getTotalPrice());
        FlightSlot flightSlot = flightSlotDAO.findById(fb.getFlightSlot().getId()).orElse(null);
        log.info("flight slot : {}", flightSlot.getCarryOnLuggage());
        flightSlot.setStatus("AVAILABLE");
        FlightSlot bg = flightSlotDAO.save(flightSlot);
        log.info("flight slot sau update: {}", bg.getStatus());
    }

    private FlightBookingDetailDto toBookingDetailDto(FlightBooking booking, Flight flight) {
        log.debug("MAPPING_BOOKING_TO_DTO    - bookingId: {}, flightId: {}", booking.getId(), flight.getId());
        FlightBookingDetailDto dto = new FlightBookingDetailDto();
        dto.setBookingId(booking.getId());
        dto.setFlight(toFlightDto(flight));
        dto.setTotalPrice(booking.getTotalPrice().doubleValue());
        dto.setStatus("BOOKED");
        dto.setCreatedAt(booking.getBookingDate());
        log.debug("MAPPING_BOOKING_TO_DTO_DONE - bookingId: {}", booking.getId());
        return dto;
    }

    private FlightDto toFlightDto(Flight flight) {
        log.debug("MAPPING_FLIGHT_TO_DTO     - flightId: {}", flight.getId());
        FlightDto dto = new FlightDto();
        dto.setId(flight.getId());
        dto.setFlightNumber(flight.getFlightNumber());
        dto.setName(flight.getName());
        dto.setDepartureTime(flight.getDepartureTime());
        dto.setArrivalTime(flight.getArrivalTime());
        dto.setCreatedAt(flight.getCreatedAt());
        dto.setUpdatedAt(flight.getUpdatedAt());
        if (flight.getCategory() != null) {
            log.debug("MAPPING_CATEGORY          - categoryId: {}", flight.getCategory().getId());
            dto.setCategory(toFlightCategoryDto(flight.getCategory()));
        }
        if (flight.getOwner() != null) {
            log.debug("MAPPING_OWNER            - ownerId: {}", flight.getOwner().getId());
            dto.setOwnerId(flight.getOwner().getId());
        }
        if (flight.getArrivalAirport() != null) {
            dto.setArrivalAirport(toAirportDto(flight.getArrivalAirport()));
        }
        if (flight.getDepartureAirport() != null) {
            dto.setDepartureAirport(toAirportDto(flight.getDepartureAirport()));
        }
        if (flight.getAirline() != null) {
            dto.setAirline(toAirlineDto(flight.getAirline()));
        }
        log.debug("MAPPING_FLIGHT_TO_DTO_DONE - flightId: {}", flight.getId());
        return dto;
    }

    private AirportDto toAirportDto(Airport airport) {
        log.debug("MAPPING_AIRPORT_TO_DTO    - airportId: {}", airport.getId());
        AirportDto dto = AirportDto.builder()
                .id(airport.getId())
                .name(airport.getName())
                .build();
        log.debug("MAPPING_AIRPORT_TO_DTO_DONE - airportId: {}", airport.getId());
        return dto;
    }

    private AirlineDto toAirlineDto(Airline airline) {
        log.debug("MAPPING_AIRLINE_TO_DTO    - airlineId: {}", airline.getId());
        AirlineDto dto = AirlineDto.builder()
                .id(airline.getId())
                .name(airline.getName())
                .build();
        log.debug("MAPPING_AIRLINE_TO_DTO_DONE - airlineId: {}", airline.getId());
        return dto;
    }

    private FlightCategoryDto toFlightCategoryDto(FlightCategory category) {
        log.debug("MAPPING_CATEGORY_TO_DTO   - categoryId: {}", category.getId());
        FlightCategoryDto dto = FlightCategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
        log.debug("MAPPING_CATEGORY_TO_DTO_DONE - categoryId: {}", category.getId());
        return dto;
    }


    private static OrderDto toOrderDto(Order entity) {
        if (entity == null) return null;
        OrderDto dto = new OrderDto();
        dto.setId(entity.getId());
        dto.setUserId(entity.getUser() != null ? entity.getUser().getId() : null);
        dto.setAmount(entity.getAmount());
        dto.setStatus(entity.getStatus());
        dto.setPayDate(entity.getPayDate());
        dto.setVoucherId(entity.getVoucher() != null ? entity.getVoucher().getId() : null);
        dto.setDestinationId(entity.getDestination() != null ? entity.getDestination().getId() : null);
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setMainProduct("");
        dto.setExpiresAt(entity.getExpiresAt());
        return dto;
    }
    private static CustomerDto toCustomerDto(Customer entity) {
        if (entity == null) return null;
        CustomerDto dto = new CustomerDto();
        dto.setId(entity.getId());
        dto.setFullName(entity.getFullName());
        dto.setGender(entity.getGender());
        dto.setDob(entity.getDob());
        dto.setPassport(entity.getPassport());
        dto.setEmail(entity.getEmail());
        dto.setPhone(entity.getPhone());
        return dto;
    }
    private static FlightSlotDto toFlightSlotDto(FlightSlot entity) {
        if (entity == null) return null;
        FlightSlotDto dto = new FlightSlotDto();
        dto.setId(entity.getId());
        dto.setPrice(entity.getPrice());
        dto.setIsBusiness(entity.getIsBusiness());
        dto.setSeatNumber(entity.getSeatNumber());
        dto.setIsWindow(entity.getIsWindow());
        dto.setIsAisle(entity.getIsAisle());
        dto.setFlightId(entity.getFlight() != null ? entity.getFlight().getId() : null);
        dto.setCarryOnLuggage(entity.getCarryOnLuggage());
        return dto;
    }
}

