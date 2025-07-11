package backend.backend.service.implement;

import backend.backend.dao.FlightBookingDAO;
import backend.backend.dao.FlightDAO;
import backend.backend.dao.FlightSlotDAO;
import backend.backend.dto.*;
import backend.backend.entity.*;
import backend.backend.service.FlightBookingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Override
    public FlightBookingDetailDto bookFlight(FlightBookingDto bookingDto) {
        String requestId = UUID.randomUUID().toString();
        log.info("BOOK_FLIGHT_REQUEST      - RequestId: {}, payload: {}", requestId, bookingDto);
        try {
            // Lấy slot
            FlightSlot slot = flightSlotDAO.findById(bookingDto.getFlightSlotId()).orElse(null);
            if (slot == null) {
                log.warn("BOOK_FLIGHT_SLOT_NOT_FOUND - RequestId: {}, slotId: {}", requestId, bookingDto.getFlightSlotId());
                return null;
            }

            // Tạo booking
            FlightBooking booking = new FlightBooking();
            booking.setFlightSlot(slot);
            booking.setBookingDate(LocalDateTime.now());
            if (bookingDto.getCustomerId() != null) {
                Customer c = new Customer(); c.setId(bookingDto.getCustomerId());
                booking.setCustomer(c);
            }
            if (bookingDto.getTicketDetailId() != null) {
                TicketDetail td = new TicketDetail(); td.setId(bookingDto.getTicketDetailId());
                booking.setTicketDetail(td);
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
            FlightBooking booking = flightBookingDAO.findById(paymentRequestDto.getBookingId()).orElse(null);
            if (booking == null) {
                log.warn("PAY_FLIGHT_BOOKING_NOT_FOUND - RequestId: {}, bookingId: {}", requestId, paymentRequestDto.getBookingId());
                return null;
            }
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

    @Override
    public FlightBookingDetailDto getFlightBookingDetail(Integer bookingId) {
        String requestId = UUID.randomUUID().toString();
        log.info("GET_BOOKING_DETAIL_REQ   - RequestId: {}, bookingId: {}", requestId, bookingId);
        try {
            FlightBooking booking = flightBookingDAO.findById(bookingId).orElse(null);
            if (booking == null) {
                log.warn("GET_BOOKING_DETAIL_NOT_FOUND - RequestId: {}, bookingId: {}", requestId, bookingId);
                return null;
            }
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
            FlightBooking booking = flightBookingDAO.findById(bookingId).orElse(null);
            if (booking == null) {
                log.warn("CANCEL_BOOKING_NOT_FOUND - RequestId: {}, bookingId: {}", requestId, bookingId);
                return null;
            }
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

    private FlightBookingDetailDto toBookingDetailDto(FlightBooking booking, Flight flight) {
        log.debug("MAPPING_BOOKING_TO_DTO    - bookingId: {}, flightId: {}", booking.getId(), flight.getId());
        FlightBookingDetailDto dto = new FlightBookingDetailDto();
        dto.setBookingId(booking.getId());
        dto.setFlight(toFlightDto(flight));
        dto.setTotalPrice(booking.getFlightSlot().getPrice().doubleValue());
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
            dto.setCategoryId(flight.getCategory().getId());
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
}
