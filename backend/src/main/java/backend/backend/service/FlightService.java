package backend.backend.service;

import backend.backend.dto.FlightDto;
import backend.backend.dto.FlightSearchRequestDto;
import backend.backend.dto.FindAvailableSlotRequestDto;
import backend.backend.dto.FlightSlotDto;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface FlightService {
    List<FlightDto> searchFlights(FlightSearchRequestDto request);
    FlightDto getFlightDetail(Integer flightId);
    int getAvailableSeats(Integer flightId);
    List<FlightDto> getAllFlights();
    Map<String, Integer> getAvailableSeatsDetail(Integer flightId);
    
    /**
     * Tìm tối đa 1 vé flight slot khả dụng dựa trên các điều kiện
     * @param request DTO chứa các điều kiện tìm kiếm
     * @return Optional<FlightSlotDto> - vé khả dụng đầu tiên tìm được
     */
    Optional<FlightSlotDto> findFirstAvailableSlot(FindAvailableSlotRequestDto request);
} 