package backend.backend.service;

import backend.backend.dto.*;
import backend.backend.entity.FlightBooking;

import java.util.List;

public interface FlightBookingService {
    FlightBookingDetailDto bookFlight(FlightBookingDto bookingDto);
    PaymentStatusDto payForFlight(PaymentRequestDto paymentRequestDto);
    List<FlightBookingDetailDto> getCustomerFlightBookings(Integer customerId);
    FlightBookingDetailDto getFlightBookingDetail(Integer bookingId);
    PaymentStatusDto cancelFlightBooking(Integer bookingId);
    FlightOrderReservationDto getFlightReservationSummary(Integer orderId);
    FlightBooking createFlightBooking(Integer orderId, AddItemRequestDto genericRequest);
} 