package backend.backend.service;

import backend.backend.dto.*;

import java.util.List;

public interface AdminFlightService {

    // Quản lý chuyến bay
    List<FlightDto> getFlights(int page, int size, String filter);

    FlightDto getFlightDetail(Integer flightId);

    FlightDto createFlight(FlightDto flightDto);
    
    FlightDto createFlightWithDetails(CreateFlightRequestDto requestDto);

    FlightDto updateFlight(Integer flightId, FlightDto flightDto);

    void deleteFlight(Integer flightId);

    // Quản lý ghế
    List<FlightSlotDto> getSeats(Integer flightId);
    List<FlightSlotDto> getSeatsBooked(Integer flightId);
    List<FlightSlotDto> updateSeats(Integer flightId, List<FlightSlotDto> seats);

    FlightSlotDto updateSeat(Integer slotId, FlightSlotDto slotDto);
    void deleteSeat(Integer slotId);

    // Quản lý đặt vé
    List<FlightBookingDetailDto> getFlightBookings(String filter);

    FlightBookingDetailDto getFlightBookingDetail(Integer bookingId);

    FlightBookingDetailDto updateFlightBookingStatus(Integer bookingId, String status);

    void updateGroupSeat(Integer flightId, FlightSeatGroupDto dto);

    // Thống kê
    List<FlightStatisticsDto> getFlightStatistics(String type, String value);

    // Quản lý sân bay
    List<AirportDto> getAirports();

    AirportDto createAirport(AirportDto airportDto);

    AirportDto updateAirport(Integer airportId, AirportDto airportDto);

    void deleteAirport(Integer airportId);

    List<ImageDto> updateFlightImages(Integer flightId, List<org.springframework.web.multipart.MultipartFile> files, List<Integer> keepImageIds);
    
    List<ImageDto> uploadFlightImages(Integer flightId, List<org.springframework.web.multipart.MultipartFile> files);
    
    // Thêm method mới cho xử lý ảnh riêng biệt
    void deleteFlightImage(Integer flightId, Integer imageId);
    
    List<ImageDto> addFlightImages(Integer flightId, List<org.springframework.web.multipart.MultipartFile> files);
}