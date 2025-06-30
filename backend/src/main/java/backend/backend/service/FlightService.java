package backend.backend.service;

import backend.backend.dto.FlightDto;
import backend.backend.dto.FlightDetailDto;
import backend.backend.dto.FlightSearchRequestDto;
import java.util.List;

public interface FlightService {
    List<FlightDto> searchFlights(FlightSearchRequestDto request);
    FlightDetailDto getFlightDetail(Long flightId);
    List<FlightDto> getAllFlights();
} 