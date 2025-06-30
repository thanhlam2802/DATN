package backend.backend.service.implement;

import backend.backend.dao.FlightDAO;
import backend.backend.entity.Flight;
import backend.backend.entity.FlightSlot;
import backend.backend.dto.FlightDto;
import backend.backend.dto.FlightDetailDto;
import backend.backend.dto.FlightSearchRequestDto;
import backend.backend.dto.SeatDto;
import backend.backend.service.FlightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FlightServiceImpl implements FlightService {
    @Autowired
    private FlightDAO flightDAO;

    @Override
    public List<FlightDto> searchFlights(FlightSearchRequestDto request) {
        // Lấy tất cả chuyến bay, filter theo request
        List<Flight> flights = flightDAO.findAll();
        return flights.stream()
                .filter(f -> request.getDeparture() == null || f.getDepartureAirport().equalsIgnoreCase(request.getDeparture()))
                .filter(f -> request.getDestination() == null || f.getArrivalAirport().equalsIgnoreCase(request.getDestination()))
                .filter(f -> request.getDepartureDate() == null || f.getDepartureTime().toLocalDate().equals(request.getDepartureDate()))
                .map(this::toFlightDto)
                .collect(Collectors.toList());
    }

    @Override
    public FlightDetailDto getFlightDetail(Long flightId) {
        Flight flight = flightDAO.findById(flightId).orElse(null);
        if (flight == null) return null;
        return toFlightDetailDto(flight);
    }

    @Override
    public List<FlightDto> getAllFlights() {
        return flightDAO.findAll().stream().map(this::toFlightDto).collect(Collectors.toList());
    }

    private FlightDto toFlightDto(Flight flight) {
        FlightDto dto = new FlightDto();
        dto.setId(flight.getId().longValue());
        dto.setCode(flight.getFlightNumber());
        dto.setAirline(flight.getName()); // Có thể thay đổi nếu có trường airline thực tế
        dto.setDeparture(flight.getDepartureAirport());
        dto.setDestination(flight.getArrivalAirport());
        dto.setDepartureTime(flight.getDepartureTime());
        dto.setArrivalTime(flight.getArrivalTime());
        dto.setFlightCategory(flight.getCategory() != null ? flight.getCategory().getName() : null);
        // Lấy giá vé thấp nhất và số ghế còn trống tổng
        if (flight.getFlightSlots() != null && !flight.getFlightSlots().isEmpty()) {
            dto.setPrice(flight.getFlightSlots().stream().map(s -> s.getPrice().doubleValue()).min(Double::compareTo).orElse(0.0));
            dto.setAvailableSeats(flight.getFlightSlots().stream().mapToInt(s -> s.getCapacity() != null ? s.getCapacity() : 0).sum());
            dto.setSeatClass(flight.getFlightSlots().get(0).getSeatClass());
        }
        return dto;
    }

    private FlightDetailDto toFlightDetailDto(Flight flight) {
        FlightDetailDto dto = new FlightDetailDto();
        dto.setId(flight.getId().longValue());
        dto.setCode(flight.getFlightNumber());
        dto.setAirline(flight.getName());
        dto.setDeparture(flight.getDepartureAirport());
        dto.setDestination(flight.getArrivalAirport());
        dto.setDepartureTime(flight.getDepartureTime());
        dto.setArrivalTime(flight.getArrivalTime());
        dto.setFlightCategory(flight.getCategory() != null ? flight.getCategory().getName() : null);
        dto.setDescription(""); // Có thể bổ sung mô tả nếu có
        if (flight.getFlightSlots() != null && !flight.getFlightSlots().isEmpty()) {
            dto.setPrice(flight.getFlightSlots().stream().map(s -> s.getPrice().doubleValue()).min(Double::compareTo).orElse(0.0));
            dto.setAvailableSeats(flight.getFlightSlots().stream().mapToInt(s -> s.getCapacity() != null ? s.getCapacity() : 0).sum());
            dto.setSeatClass(flight.getFlightSlots().get(0).getSeatClass());
            dto.setSeats(flight.getFlightSlots().stream().map(this::toSeatDto).collect(Collectors.toList()));
        }
        return dto;
    }

    private SeatDto toSeatDto(FlightSlot slot) {
        SeatDto dto = new SeatDto();
        dto.setId(slot.getId().longValue());
        dto.setCode(slot.getSeatClass());
        dto.setStatus("AVAILABLE"); // Có thể bổ sung logic lấy trạng thái thực tế
        dto.setPrice(slot.getPrice().doubleValue());
        dto.setSeatClass(slot.getSeatClass());
        return dto;
    }
} 