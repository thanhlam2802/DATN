package backend.backend.service;

import backend.backend.dto.*;

import java.util.List;

public interface AdminFlightService {

    // Quản lý chuyến bay
    List<FlightDto> getFlights(int page, int size, String filter);

    FlightDetailDto getFlightDetail(Long flightId);

    FlightDto createFlight(FlightDto flightDto);

    FlightDto updateFlight(Long flightId, FlightDto flightDto);

    void deleteFlight(Long flightId);

    // Quản lý ghế
    List<SeatDto> getSeats(Long flightId);

    List<SeatDto> updateSeats(Long flightId, List<SeatDto> seats);

    // Quản lý đặt vé
    List<FlightBookingDetailDto> getFlightBookings(String filter);

    FlightBookingDetailDto getFlightBookingDetail(Long bookingId);

    FlightBookingDetailDto updateFlightBookingStatus(Long bookingId, String status);

    // Thống kê
    List<FlightStatisticsDto> getFlightStatistics(String type, String value);

    // Quản lý sân bay (airline)
    List<AirlineDto> getAirlines();

    AirlineDto createAirline(AirlineDto airlineDto);

    AirlineDto updateAirline(Long airlineId, AirlineDto airlineDto);

    void deleteAirline(Long airlineId);
}