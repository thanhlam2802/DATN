package backend.backend.controller;

import backend.backend.dto.FlightBookingDetailDto;
import backend.backend.dto.FlightBookingDto;
import backend.backend.dto.PaymentRequestDto;
import backend.backend.dto.PaymentStatusDto;
import backend.backend.service.FlightBookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
public class FlightBookingController {
    @Autowired
    private FlightBookingService flightBookingService;

    @PostMapping("/api/bookings/flights")
    public FlightBookingDetailDto bookFlight(@RequestBody FlightBookingDto bookingDto) {
        return flightBookingService.bookFlight(bookingDto);
    }

    @PostMapping("/api/payments/flights")
    public PaymentStatusDto payForFlight(@RequestBody PaymentRequestDto paymentRequestDto) {
        return flightBookingService.payForFlight(paymentRequestDto);
    }

    @GetMapping("/api/users/{userId}/flight-bookings")
    public List<FlightBookingDetailDto> getUserFlightBookings(@PathVariable Long userId) {
        return flightBookingService.getUserFlightBookings(userId);
    }

    @GetMapping("/api/bookings/flights/{bookingId}")
    public FlightBookingDetailDto getFlightBookingDetail(@PathVariable Long bookingId) {
        return flightBookingService.getFlightBookingDetail(bookingId);
    }

    @PostMapping("/api/bookings/flights/{bookingId}/cancel")
    public PaymentStatusDto cancelFlightBooking(@PathVariable Long bookingId) {
        return flightBookingService.cancelFlightBooking(bookingId);
    }
} 