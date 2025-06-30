package backend.backend.controller;

import backend.backend.dto.FlightDto;
import backend.backend.dto.FlightDetailDto;
import backend.backend.dto.FlightSearchRequestDto;
import backend.backend.service.FlightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/flights")
public class FlightController {
    @Autowired
    private FlightService flightService;

    @GetMapping("/search")
    public List<FlightDto> searchFlights(FlightSearchRequestDto request) {
        return flightService.searchFlights(request);
    }

    @GetMapping("/{flightId}")
    public FlightDetailDto getFlightDetail(@PathVariable Long flightId) {
        return flightService.getFlightDetail(flightId);
    }
} 