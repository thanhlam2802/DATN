package backend.backend.dto;

import lombok.Data;

@Data
public class FlightOrderReservationDto {
    private OrderDto order;
    private FlightBookingDetailDto booking;
    private CustomerDto customer;
    private FlightSlotDto flightSlot;
} 