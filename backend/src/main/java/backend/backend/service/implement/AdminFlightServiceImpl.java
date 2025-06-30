package backend.backend.service.implement;

import backend.backend.dao.FlightDAO;
import backend.backend.dao.FlightBookingDAO;
import backend.backend.dao.AirlineDAO;
import backend.backend.entity.Flight;
import backend.backend.entity.FlightBooking;
import backend.backend.entity.FlightSlot;
import backend.backend.entity.Airline;
import backend.backend.dto.*;
import backend.backend.service.AdminFlightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;
import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class AdminFlightServiceImpl implements AdminFlightService {
    @Autowired
    private FlightDAO flightDAO;
    @Autowired
    private FlightBookingDAO flightBookingDAO;
    @Autowired
    private AirlineDAO airlineDAO;

    // Quản lý chuyến bay
    @Override
    public List<FlightDto> getFlights(int page, int size, String filter) {
        List<Flight> flights = flightDAO.findAll();
        return flights.stream().map(this::toFlightDto).collect(Collectors.toList());
    }

    @Override
    public FlightDetailDto getFlightDetail(Long flightId) {
        Flight flight = flightDAO.findById(flightId).orElse(null);
        if (flight == null) return null;
        return toFlightDetailDto(flight);
    }

    @Override
    public FlightDto createFlight(FlightDto flightDto) {
        Flight flight = new Flight();
        flight.setFlightNumber(flightDto.getCode());
        flight.setName(flightDto.getAirline());
        flight.setDepartureAirport(flightDto.getDeparture());
        flight.setArrivalAirport(flightDto.getDestination());
        flight.setDepartureTime(flightDto.getDepartureTime());
        flight.setArrivalTime(flightDto.getArrivalTime());
        // ... mapping các trường khác nếu cần
        flightDAO.save(flight);
        return toFlightDto(flight);
    }

    @Override
    public FlightDto updateFlight(Long flightId, FlightDto flightDto) {
        Flight flight = flightDAO.findById(flightId).orElse(null);
        if (flight == null) return null;
        flight.setFlightNumber(flightDto.getCode());
        flight.setName(flightDto.getAirline());
        flight.setDepartureAirport(flightDto.getDeparture());
        flight.setArrivalAirport(flightDto.getDestination());
        flight.setDepartureTime(flightDto.getDepartureTime());
        flight.setArrivalTime(flightDto.getArrivalTime());
        // ... mapping các trường khác nếu cần
        flightDAO.save(flight);
        return toFlightDto(flight);
    }

    @Override
    public void deleteFlight(Long flightId) {
        flightDAO.deleteById(flightId);
    }

    // Quản lý ghế
    @Override
    public List<SeatDto> getSeats(Long flightId) {
        Flight flight = flightDAO.findById(flightId).orElse(null);
        if (flight == null || flight.getFlightSlots() == null) return null;
        return flight.getFlightSlots().stream().map(this::toSeatDto).collect(Collectors.toList());
    }

    @Override
    public List<SeatDto> updateSeats(Long flightId, List<SeatDto> seats) {
        Flight flight = flightDAO.findById(flightId).orElse(null);
        if (flight == null || flight.getFlightSlots() == null) return null;
        for (SeatDto seatDto : seats) {
            flight.getFlightSlots().stream()
                .filter(s -> s.getId().longValue() == seatDto.getId())
                .findFirst()
                .ifPresent(slot -> {
                    slot.setSeatClass(seatDto.getSeatClass());
                    slot.setPrice(BigDecimal.valueOf(seatDto.getPrice()));
                    slot.setCapacity((int) seatDto.getPrice()); // Có thể sửa lại nếu seatDto có trường capacity
                });
        }
        return flight.getFlightSlots().stream().map(this::toSeatDto).collect(Collectors.toList());
    }

    // Quản lý đặt vé
    @Override
    public List<FlightBookingDetailDto> getFlightBookings(String filter) {
        List<FlightBooking> bookings = flightBookingDAO.findAll();
        return bookings.stream().map(b -> toBookingDetailDto(b, b.getFlightSlot().getFlight())).collect(Collectors.toList());
    }

    @Override
    public FlightBookingDetailDto getFlightBookingDetail(Long bookingId) {
        FlightBooking booking = flightBookingDAO.findById(bookingId).orElse(null);
        if (booking == null) return null;
        return toBookingDetailDto(booking, booking.getFlightSlot().getFlight());
    }

    @Override
    public FlightBookingDetailDto updateFlightBookingStatus(Long bookingId, String status) {
        FlightBooking booking = flightBookingDAO.findById(bookingId).orElse(null);
        if (booking == null) return null;
        // Giả lập cập nhật trạng thái
        return toBookingDetailDto(booking, booking.getFlightSlot().getFlight());
    }

    // Thống kê
    @Override
    public List<FlightStatisticsDto> getFlightStatistics(String type, String value) {
        List<Flight> flights = flightDAO.findAll();
        return flights.stream().map(f -> {
            FlightStatisticsDto dto = new FlightStatisticsDto();
            dto.setFlightId(f.getId().longValue());
            dto.setFlightCode(f.getFlightNumber());
            dto.setDate(LocalDate.now());
            int totalTickets = f.getFlightSlots() != null ? f.getFlightSlots().stream().mapToInt(s -> s.getCapacity() != null ? s.getCapacity() : 0).sum() : 0;
            int soldTickets = 0;
            double revenue = 0;
            if (f.getFlightSlots() != null) {
                for (FlightSlot slot : f.getFlightSlots()) {
                    List<FlightBooking> bookings = flightBookingDAO.findByFlightSlotId(slot.getId().longValue());
                    for (FlightBooking b : bookings) {
                        soldTickets += b.getNumPassengers();
                        revenue += b.getTotalPrice().doubleValue();
                    }
                }
            }
            dto.setTotalTickets(totalTickets);
            dto.setSoldTickets(soldTickets);
            dto.setRevenue(revenue);
            dto.setOccupancyRate(totalTickets > 0 ? (double) soldTickets / totalTickets * 100 : 0);
            return dto;
        }).collect(Collectors.toList());
    }

    // Quản lý sân bay (airline)
    @Override
    public List<AirlineDto> getAirlines() {
        List<Airline> airlines = airlineDAO.findAll();
        return airlines.stream().map(this::toAirlineDto).collect(Collectors.toList());
    }

    @Override
    public AirlineDto createAirline(AirlineDto airlineDto) {
        Airline airline = new Airline();
        airline.setName(airlineDto.getName());
        airline.setCode(airlineDto.getCode());
        airline.setLocation(airlineDto.getLocation());
        airline.setDescription(airlineDto.getDescription());
        airlineDAO.save(airline);
        return toAirlineDto(airline);
    }

    @Override
    public AirlineDto updateAirline(Long airlineId, AirlineDto airlineDto) {
        Airline airline = airlineDAO.findById(airlineId).orElse(null);
        if (airline == null) return null;
        airline.setName(airlineDto.getName());
        airline.setCode(airlineDto.getCode());
        airline.setLocation(airlineDto.getLocation());
        airline.setDescription(airlineDto.getDescription());
        airlineDAO.save(airline);
        return toAirlineDto(airline);
    }

    @Override
    public void deleteAirline(Long airlineId) {
        airlineDAO.deleteById(airlineId);
    }

    // Mapping helpers (giữ nguyên như trước)
    private FlightDto toFlightDto(Flight flight) { /* ... */ return null; }
    private FlightDetailDto toFlightDetailDto(Flight flight) { /* ... */ return null; }
    private SeatDto toSeatDto(FlightSlot slot) { /* ... */ return null; }
    private FlightBookingDetailDto toBookingDetailDto(FlightBooking booking, Flight flight) { /* ... */ return null; }
    private AirlineDto toAirlineDto(Airline airline) { /* ... */ return null; }
} 