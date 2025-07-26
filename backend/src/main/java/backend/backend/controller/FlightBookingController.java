package backend.backend.controller;

import backend.backend.dto.*;
import backend.backend.entity.FlightBooking;
import backend.backend.service.FlightBookingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import backend.backend.dao.FlightBookingDAO;
import backend.backend.service.OrderService;
import backend.backend.service.CustomerService;

@Slf4j
@RestController
@RequestMapping("/api")
public class FlightBookingController {

    @Autowired
    private FlightBookingService flightBookingService;

    @Autowired
    private FlightBookingDAO flightBookingDAO;

    @Autowired
    private OrderService orderService;

    @Autowired
    private CustomerService customerService;

    @PostMapping("/bookings/flights")
    public ResponseEntity<FlightBookingDetailDto> bookFlight(@RequestBody FlightBookingDto bookingDto) {
        String requestId = UUID.randomUUID().toString();
        log.info("BOOK_FLIGHT_REQUEST - RequestId: {}, Payload: {}", requestId, bookingDto);
        try {
            FlightBookingDetailDto detail = flightBookingService.bookFlight(bookingDto);
            log.info("BOOK_FLIGHT_SUCCESS - RequestId: {}, BookingId: {}", requestId, detail.getBookingId());
            return ResponseEntity.ok(detail);
        } catch (Exception e) {
            log.error("BOOK_FLIGHT_FAILED - RequestId: {}, Error: {}", requestId, e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping("/payments/flights")
    public ResponseEntity<PaymentStatusDto> payForFlight(@RequestBody PaymentRequestDto paymentRequestDto) {
        String requestId = UUID.randomUUID().toString();
        log.info("PAY_FLIGHT_REQUEST - RequestId: {}, Payload: {}", requestId, paymentRequestDto);
        try {
            PaymentStatusDto status = flightBookingService.payForFlight(paymentRequestDto);
            log.info("PAY_FLIGHT_SUCCESS - RequestId: {}, BookingId: {}, Status: {}",
                    requestId, paymentRequestDto.getBookingId(), status.getStatus());
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            log.error("PAY_FLIGHT_FAILED - RequestId: {}, BookingId: {}, Error: {}",
                    requestId, paymentRequestDto.getBookingId(), e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/customers/{customerId}/flight-bookings")
    public ResponseEntity<List<FlightBookingDetailDto>> getCustomerFlightBookings(@PathVariable Integer customerId) {
        String requestId = UUID.randomUUID().toString();
        log.info("GET_CUSTOMER_BOOKINGS_REQUEST - RequestId: {}, CustomerId: {}", requestId, customerId);
        try {
            List<FlightBookingDetailDto> list = flightBookingService.getCustomerFlightBookings(customerId);
            log.info("GET_CUSTOMER_BOOKINGS_SUCCESS - RequestId: {}, CustomerId: {}, Count: {}",
                    requestId, customerId, list.size());
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            log.error("GET_CUSTOMER_BOOKINGS_FAILED - RequestId: {}, CustomerId: {}, Error: {}",
                    requestId, customerId, e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/bookings/flights/{bookingId}")
    public ResponseEntity<FlightBookingDetailDto> getFlightBookingDetail(@PathVariable Integer bookingId) {
        String requestId = UUID.randomUUID().toString();
        log.info("GET_BOOKING_DETAIL_REQUEST - RequestId: {}, BookingId: {}", requestId, bookingId);
        try {
            FlightBookingDetailDto detail = flightBookingService.getFlightBookingDetail(bookingId);
            log.info("GET_BOOKING_DETAIL_SUCCESS - RequestId: {}, BookingId: {}", requestId, bookingId);
            return ResponseEntity.ok(detail);
        } catch (Exception e) {
            log.error("GET_BOOKING_DETAIL_FAILED - RequestId: {}, BookingId: {}, Error: {}",
                    requestId, bookingId, e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping("/bookings/flights/{bookingId}/cancel")
    public ResponseEntity<PaymentStatusDto> cancelFlightBooking(@PathVariable Integer bookingId) {
        String requestId = UUID.randomUUID().toString();
        log.info("CANCEL_BOOKING_REQUEST - RequestId: {}, BookingId: {}", requestId, bookingId);
        try {
            PaymentStatusDto status = flightBookingService.cancelFlightBooking(bookingId);
            log.info("CANCEL_BOOKING_SUCCESS - RequestId: {}, BookingId: {}, Status: {}",
                    requestId, bookingId, status.getStatus());
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            log.error("CANCEL_BOOKING_FAILED - RequestId: {}, BookingId: {}, Error: {}",
                    requestId, bookingId, e.getMessage(), e);
            throw e;
        }
    }

    // API tổng hợp: Lấy thông tin giữ chỗ chuyến bay (reservation summary) theo orderId
    @GetMapping("/bookings/flights/reservation-summary/{bookingId}")
    public ResponseEntity<FlightOrderReservationDto> getFlightReservationSummary(@PathVariable Integer bookingId) {
        FlightOrderReservationDto dto = flightBookingService.getFlightReservationSummary(bookingId);
        if (dto == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(dto);
    }
}
