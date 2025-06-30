package backend.backend.controller;

import backend.backend.dto.*;
import backend.backend.service.AdminFlightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminFlightController {
    @Autowired
    private AdminFlightService adminFlightService;

    // Quản lý chuyến bay
    @GetMapping("/flights")
    public List<FlightDto> getFlights(@RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "10") int size,
                                      @RequestParam(required = false) String filter) {
        return adminFlightService.getFlights(page, size, filter);
    }

    @GetMapping("/flights/{flightId}")
    public FlightDetailDto getFlightDetail(@PathVariable Long flightId) {
        return adminFlightService.getFlightDetail(flightId);
    }

    @PostMapping("/flights")
    public FlightDto createFlight(@RequestBody FlightDto flightDto) {
        return adminFlightService.createFlight(flightDto);
    }

    @PutMapping("/flights/{flightId}")
    public FlightDto updateFlight(@PathVariable Long flightId, @RequestBody FlightDto flightDto) {
        return adminFlightService.updateFlight(flightId, flightDto);
    }

    @DeleteMapping("/flights/{flightId}")
    public void deleteFlight(@PathVariable Long flightId) {
        adminFlightService.deleteFlight(flightId);
    }

    // Quản lý ghế
    @GetMapping("/flights/{flightId}/seats")
    public List<SeatDto> getSeats(@PathVariable Long flightId) {
        return adminFlightService.getSeats(flightId);
    }

    @PutMapping("/flights/{flightId}/seats")
    public List<SeatDto> updateSeats(@PathVariable Long flightId, @RequestBody List<SeatDto> seats) {
        return adminFlightService.updateSeats(flightId, seats);
    }

    // Quản lý đặt vé
    @GetMapping("/flight-bookings")
    public List<FlightBookingDetailDto> getFlightBookings(@RequestParam(required = false) String filter) {
        return adminFlightService.getFlightBookings(filter);
    }

    @GetMapping("/flight-bookings/{bookingId}")
    public FlightBookingDetailDto getFlightBookingDetail(@PathVariable Long bookingId) {
        return adminFlightService.getFlightBookingDetail(bookingId);
    }

    @PutMapping("/flight-bookings/{bookingId}")
    public FlightBookingDetailDto updateFlightBookingStatus(@PathVariable Long bookingId, @RequestParam String status) {
        return adminFlightService.updateFlightBookingStatus(bookingId, status);
    }

    // Thống kê
    @GetMapping("/flights/statistics")
    public List<FlightStatisticsDto> getFlightStatistics(@RequestParam String type, @RequestParam String value) {
        return adminFlightService.getFlightStatistics(type, value);
    }

    // Quản lý sân bay (airline)
    @GetMapping("/airlines")
    public List<AirlineDto> getAirlines() {
        return adminFlightService.getAirlines();
    }

    @PostMapping("/airlines")
    public AirlineDto createAirline(@RequestBody AirlineDto airlineDto) {
        return adminFlightService.createAirline(airlineDto);
    }

    @PutMapping("/airlines/{airlineId}")
    public AirlineDto updateAirline(@PathVariable Long airlineId, @RequestBody AirlineDto airlineDto) {
        return adminFlightService.updateAirline(airlineId, airlineDto);
    }

    @DeleteMapping("/airlines/{airlineId}")
    public void deleteAirline(@PathVariable Long airlineId) {
        adminFlightService.deleteAirline(airlineId);
    }
} 