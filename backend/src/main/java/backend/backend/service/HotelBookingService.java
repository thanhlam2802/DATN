package backend.backend.service;

import backend.backend.dto.Hotel.HotelBookingRequestDto;
import backend.backend.dto.OrderDto;
import org.springframework.security.core.Authentication;

public interface HotelBookingService {
    OrderDto bookHotel(HotelBookingRequestDto dto, Authentication authentication);
} 