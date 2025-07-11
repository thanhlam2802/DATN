package backend.backend.service;

import backend.backend.dto.FlightDto;
import backend.backend.dto.FlightSearchRequestDto;
import java.util.List;
import java.util.Map;

public interface FlightService {
    List<FlightDto> searchFlights(FlightSearchRequestDto request);
    FlightDto getFlightDetail(Integer flightId);
    int getAvailableSeats(Integer flightId);
    List<FlightDto> getAllFlights();
    Map<String, Integer> getAvailableSeatsDetail(Integer flightId);
} 