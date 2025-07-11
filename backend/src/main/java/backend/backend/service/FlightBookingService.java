package backend.backend.service;

import backend.backend.dto.FlightBookingDetailDto;
import backend.backend.dto.FlightBookingDto;
import backend.backend.dto.PaymentRequestDto;
import backend.backend.dto.PaymentStatusDto;
import java.util.List;

public interface FlightBookingService {
    FlightBookingDetailDto bookFlight(FlightBookingDto bookingDto);
    PaymentStatusDto payForFlight(PaymentRequestDto paymentRequestDto);
    List<FlightBookingDetailDto> getCustomerFlightBookings(Integer customerId);
    FlightBookingDetailDto getFlightBookingDetail(Integer bookingId);
    PaymentStatusDto cancelFlightBooking(Integer bookingId);
} 